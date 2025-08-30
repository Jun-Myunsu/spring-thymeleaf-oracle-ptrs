// src/main/resources/static/js/common/api.js
class ApiError extends Error {
  constructor(message, { status, url, body } = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.url = url;
    this.body = body;
  }
}

class ApiClient {
  constructor({
    baseURL = '',
    defaultHeaders = {},
    timeoutMs = 10_000,
    retry = { attempts: 1, factor: 2, baseDelay: 300 }
  } = {}) {
    this.baseURL = baseURL.replace(/\/$/, '');
    this.defaultHeaders = { Accept: 'application/json', ...defaultHeaders };
    this.timeoutMs = timeoutMs;
    this.retry = retry;

    this.cache = new Map();       // fullUrl -> { data, etag, time }
    this.controllers = new Map(); // fullUrl -> AbortController
  }

  // ===== Public APIs =====
  async get(url, { useCache = true, useETag = true, retry = false } = {}) {
    const fullUrl = this._full(url);
    if (useCache && this.cache.has(fullUrl)) {
      return this.cache.get(fullUrl).data;
    }
    const exec = retry ? this._withRetry.bind(this) : this._request.bind(this);
    return await exec('GET', url, { useETag });
  }

  async post(url, body, opts = {}) { return this._request('POST', url, { body, ...opts }); }
  async put(url, body, opts = {})  { return this._request('PUT',  url, { body, ...opts }); }
  async patch(url, body, opts = {}){ return this._request('PATCH',url, { body, ...opts }); }
  async delete(url, opts = {})     { return this._request('DELETE',url, { ...opts }); }

  abortPreviousRequest(fullUrl) {
    const c = this.controllers.get(fullUrl);
    if (c) c.abort();
  }

  clearCache(pattern) {
    if (!pattern) return this.cache.clear();
    for (const key of this.cache.keys()) {
      if (key.includes(pattern)) this.cache.delete(key);
    }
  }

  // ===== Core =====
  async _withRetry(method, url, opts = {}) {
    const { attempts = this.retry.attempts, factor = this.retry.factor, baseDelay = this.retry.baseDelay } = this.retry;
    let tries = 0, lastErr;
    while (tries <= attempts) {
      try {
        return await this._request(method, url, opts);
      } catch (e) {
        lastErr = e;
        // 재시도 대상: 네트워크 에러(TypeError), 502/503/504
        const retriableStatus = [502, 503, 504].includes(e?.status);
        const retriableNetwork = e instanceof TypeError; // fetch 네트워크 실패
        if (!(retriableStatus || retriableNetwork) || tries === attempts) break;
        await this._sleep(baseDelay * Math.pow(factor, tries));
        tries++;
      }
    }
    throw lastErr;
  }

  async _request(method, url, { headers = {}, body, signal, useETag = true } = {}) {
    const fullUrl = this._full(url);

    this.abortPreviousRequest(fullUrl);
    const ctl = new AbortController();
    const timeoutId = setTimeout(() => ctl.abort(new DOMException('Timeout', 'AbortError')), this.timeoutMs);
    const combinedSignal = signal ? this._combineSignals(signal, ctl.signal) : ctl.signal;
    this.controllers.set(fullUrl, ctl);

    const cached = this.cache.get(fullUrl);
    const reqHeaders = this._buildHeaders(method, headers, useETag ? cached?.etag : null);
    const init = {
      method,
      headers: reqHeaders,
      body: body != null ? (typeof body === 'string' ? body : JSON.stringify(body)) : undefined,
      signal: combinedSignal
    };

    try {
      const res = await fetch(fullUrl, init);

      if (res.status === 304 && cached) return cached.data;
      if (!res.ok) throw await this._toApiError(res, fullUrl);

      const data = await this._parseBody(res, method);
      const etag = res.headers.get('ETag');
      if (etag && method === 'GET') this.cache.set(fullUrl, { data, etag, time: Date.now() });
      return data;
    } finally {
      clearTimeout(timeoutId);
      this.controllers.delete(fullUrl);
    }
  }

  // ===== Helpers =====
  _full(url) {
    return this.baseURL + url;
  }

  _buildHeaders(method, extraHeaders, etag) {
    const isRead = method === 'GET' || method === 'HEAD';
    const base = isRead ? this.defaultHeaders : { 'Content-Type': 'application/json', ...this.defaultHeaders };
    const cond = etag ? { 'If-None-Match': etag } : {};
    return { ...base, ...cond, ...extraHeaders };
  }

  async _parseBody(res, method) {
    // 본문 없는 케이스: 204, HEAD
    if (res.status === 204 || method === 'HEAD') return null;

    const ct = res.headers.get('Content-Type') || '';
    if (ct.includes('application/json')) return await res.json();

    // 그 외는 텍스트로 수용(필요 시 확장)
    return await res.text();
  }

  async _toApiError(res, url) {
    let body = null;
    try { body = await res.clone().json(); }
    catch { try { body = await res.text(); } catch {} }
    return new ApiError(`HTTP ${res.status} ${res.statusText}`, { status: res.status, url, body });
  }

  _combineSignals(a, b) {
    if (!a) return b;
    if (!b) return a;
    const ctl = new AbortController();
    const abort = () => ctl.abort();
    if (a.aborted || b.aborted) ctl.abort();
    a.addEventListener('abort', abort, { once: true });
    b.addEventListener('abort', abort, { once: true });
    return ctl.signal;
  }

  _sleep(ms) { return new Promise(r => setTimeout(r, ms)); }
}

// 전역 인스턴스
window.apiClient = new ApiClient({
  // baseURL: '/api',
  timeoutMs: 8000,
  retry: { attempts: 1, factor: 2, baseDelay: 200 }
});

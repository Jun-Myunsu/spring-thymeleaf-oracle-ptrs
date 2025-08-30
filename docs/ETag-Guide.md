# ETag 캐싱 가이드

## ETag란?

ETag(Entity Tag)는 HTTP 캐싱 메커니즘으로, 리소스의 특정 버전을 식별하는 고유한 식별자입니다.

### 기본 개념

- **목적**: 불필요한 데이터 전송 방지, 네트워크 대역폭 절약
- **동작**: 클라이언트가 이전에 받은 데이터와 동일한지 확인
- **결과**: 데이터 변경 없으면 304 Not Modified 응답

## ETag 동작 과정

### 1. 첫 번째 요청
```http
GET /api/categories/roots
```

**서버 응답:**
```http
HTTP/1.1 200 OK
Cache-Control: max-age=300, public
ETag: W/"1234567890"
Content-Type: application/json

[{"id":"posco","name":"포스코",...}]
```

### 2. 두 번째 요청 (조건부)
```http
GET /api/categories/roots
If-None-Match: W/"1234567890"
```

**데이터 변경 없는 경우:**
```http
HTTP/1.1 304 Not Modified
Cache-Control: max-age=300, public
ETag: W/"1234567890"
```

**데이터 변경된 경우:**
```http
HTTP/1.1 200 OK
Cache-Control: max-age=300, public
ETag: W/"9876543210"
Content-Type: application/json

[{"id":"posco","name":"포스코 수정",...}]
```

## 프로젝트 적용

### 설정 파일

**ETagConfig.java**
```java
@Configuration
public class ETagConfig {
    
    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> etagFilter() {
        var filter = new ShallowEtagHeaderFilter();
        var reg = new FilterRegistrationBean<>(filter);
        reg.setName("ptrsEtagFilter");
        reg.addUrlPatterns("/api/categories/*");
        reg.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        reg.setOrder(1);
        return reg;
    }
}
```

### 적용 범위

- ✅ `/api/categories/roots` - 루트 카테고리 조회
- ✅ `/api/categories?parentId=xxx` - 자식 카테고리 조회
- ❌ `/api/users/*` - 사용자 API (적용 안됨)

### 컨트롤러 구현

```java
@GetMapping("/roots")
public ResponseEntity<List<CategoryDto>> roots() {
    var data = categoryMapper.findRoots();
    
    return ResponseEntity.ok()
            .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
            .body(data); // ETag는 필터가 자동 추가
}
```

## ETag 생성 방식

### ShallowEtagHeaderFilter 방식 (현재 사용)

**장점:**
- 자동화된 ETag 생성
- 컨트롤러 코드 변경 불필요
- 응답 본문 해시 기반으로 정확성 보장

**단점:**
- 응답 본문 전체를 메모리에 버퍼링
- 매 요청마다 해시 계산 오버헤드

**생성 로직:**
```java
// 내부적으로 다음과 같이 동작
String responseBody = "[{\"id\":\"posco\",\"name\":\"포스코\"}]";
String etag = "W/\"" + DigestUtils.md5DigestAsHex(responseBody.getBytes()) + "\"";
```

## 성능 효과

### 네트워크 절약
- **첫 요청**: 전체 JSON 데이터 전송 (예: 2KB)
- **재요청**: 304 응답만 전송 (예: 200B)
- **절약률**: 약 90% 대역폭 절약

### 서버 부하 감소
- DB 조회는 실행되지만 JSON 직렬화/전송 생략
- 클라이언트 캐시 활용으로 체감 속도 향상

## 테스트 방법

### cURL 테스트
```bash
# 첫 번째 요청
curl -v http://localhost:8080/api/categories/roots

# ETag 값 확인 후 조건부 요청
curl -v -H "If-None-Match: W/\"1234567890\"" \
     http://localhost:8080/api/categories/roots
```

### 브라우저 개발자 도구
1. Network 탭 열기
2. API 호출
3. Response Headers에서 ETag 확인
4. 새로고침 시 304 응답 확인

## 주의사항

### 캐시 무효화
- 카테고리 데이터 수정 시 ETag 자동 변경
- 서버 재시작 시 ETag 값 변경 가능

### 메모리 사용
- 큰 응답의 경우 메모리 사용량 증가
- 현재 카테고리 API는 소량 데이터로 문제없음

### 동시성
- 여러 사용자가 동시 접근 시에도 안전
- 각 요청별로 독립적인 ETag 생성

## 확장 방안

### 다른 API 적용
```java
// 전체 API에 적용하려면
reg.addUrlPatterns("/api/*");
```

### 커스텀 ETag 생성
```java
// 버전 기반 ETag (고성능)
.eTag("\"v" + dataVersion + "\"")

// 타임스탬프 기반 ETag
.eTag("\"" + lastModified.getTime() + "\"")
```

## 모니터링

### 로그 확인
```
2024-01-15 10:30:15.123 [http-nio-8080-exec-1] INFO  CategoryController - === 루트 카테고리 조회 시작 ===
2024-01-15 10:30:15.129 [http-nio-8080-exec-1] INFO  CategoryController - 루트 카테고리 조회 완료 - 결과 수: 3
```

### 캐시 히트율 측정
- 304 응답 비율 모니터링
- 평균 응답 시간 측정
- 네트워크 트래픽 감소량 확인
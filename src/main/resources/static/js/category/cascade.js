// src/main/resources/static/js/category/cascade.js
class CascadeSelector {
    constructor() {
        this.selects = [];
        this.spinners = [];
        this.pathDisplay = document.getElementById('path-display');
        this.levels = [];
        
        this.initializeLevels();
    }
    
    async initializeLevels() {
        // 하드코딩된 레벨 사용
        this.levels = [
            { level: 1, name: '기업' },
            { level: 2, name: '부문' },
            { level: 3, name: '사업부' },
            { level: 4, name: '제품군' },
            { level: 5, name: '제품' }
        ];
        
        this.createSelectElements();
        this.loadData(1, '/api/categories/roots');
    }
    
    createSelectElements() {
        const container = document.getElementById('category-form');
        
        this.levels.forEach(level => {
            const selectGroup = this.createElement('div', 'select-group', `
                <label for="lv${level.level}">${level.name}</label>
                <div class="select-wrapper">
                    <select id="lv${level.level}" ${level.level > 1 ? 'disabled' : ''} aria-label="${level.name} 카테고리">
                        <option value="">선택하세요</option>
                    </select>
                    <div class="spinner" id="spinner${level.level}"></div>
                </div>
            `);
            
            container.appendChild(selectGroup);
            
            this.selects[level.level] = document.getElementById(`lv${level.level}`);
            this.spinners[level.level] = document.getElementById(`spinner${level.level}`);
            this.selects[level.level].addEventListener('change', (e) => this.handleChange(level.level, e.target.value));
        });
    }
    
    createElement(tag, className, innerHTML) {
        const element = document.createElement(tag);
        element.className = className;
        element.innerHTML = innerHTML;
        return element;
    }
    
    async loadData(level, url) {
        try {
            this.showSpinner(level);
            const data = await apiClient.get(url, { 
                useCache: true, 
                useETag: true,
                retry: level === 1 // 루트 레벨만 재시도
            });
            this.populateSelect(level, data);
            this.updatePath();
        } catch (error) {
            this.handleError(error, `${this.getLevelName(level)} 로드 실패`);
        } finally {
            this.hideSpinner(level);
        }
    }
    
    async handleChange(level, value) {
        this.clearLowerLevels(level);
        this.updatePath();
        
        if (!value || !this.hasChildren(level, value)) return;
        
        const nextLevel = level + 1;
        if (nextLevel > this.getMaxLevel()) return;
        
        await this.loadData(nextLevel, `/api/categories?parentId=${value}`);
        this.selects[nextLevel].disabled = false;
    }
    

    
    populateSelect(level, data) {
        const select = this.selects[level];
        select.innerHTML = '<option value="">선택하세요</option>';
        
        data.forEach(item => {
            const option = this.createElement('option', '', '');
            option.value = item.id;
            option.textContent = item.name;
            option.dataset.hasChildren = item.hasChildren;
            select.appendChild(option);
        });
    }
    
    hasChildren(level, value) {
        const option = this.selects[level].querySelector(`option[value="${value}"]`);
        return option?.dataset.hasChildren === 'true';
    }
    
    clearLowerLevels(fromLevel) {
        for (let i = fromLevel + 1; i <= this.getMaxLevel(); i++) {
            if (this.selects[i]) {
                this.selects[i].innerHTML = '<option value="">선택하세요</option>';
                this.selects[i].disabled = true;
                this.hideSpinner(i);
            }
        }
    }
    
    updatePath() {
        const path = this.getSelectedPath();
        this.pathDisplay.textContent = path.length ? path.join(' → ') : '-';
    }
    
    getSelectedPath() {
        const path = [];
        for (let i = 1; i <= this.getMaxLevel(); i++) {
            const select = this.selects[i];
            if (select) {
                const selectedOption = select.options[select.selectedIndex];
                if (selectedOption?.value) {
                    path.push(selectedOption.textContent);
                } else {
                    break;
                }
            }
        }
        return path;
    }
    
    getMaxLevel() {
        return Math.max(...this.levels.map(l => l.level));
    }
    
    getLevelName(level) {
        return this.levels.find(l => l.level === level)?.name || `${level}단계`;
    }
    
    showSpinner(level) {
        this.spinners[level]?.classList.add('show');
    }
    
    hideSpinner(level) {
        this.spinners[level]?.classList.remove('show');
    }
    
    handleError(error, defaultMessage) {
        if (error.name === 'AbortError') return; // 사용자 취소 무시
        
        let message = defaultMessage;
        if (error instanceof ApiError) {
            if (error.status === 404) {
                message = '데이터를 찾을 수 없습니다';
            } else if (error.status >= 500) {
                message = '서버 오류가 발생했습니다';
            } else if (error.status === 0) {
                message = '네트워크 연결을 확인해주세요';
            }
        }
        
        this.showToast(message, 'error');
        console.error('카테고리 오류:', error);
    }
    
    showToast(message, type = 'error') {
        const toast = document.getElementById('toast');
        toast.textContent = message;
        toast.className = `toast ${type} show`;
        setTimeout(() => toast.classList.remove('show'), 3000);
    }
    
    // 캐시 무효화 (데이터 변경 시 사용)
    invalidateCache() {
        apiClient.clearCache('categories');
    }
    
    // 선택 초기화
    reset() {
        for (let i = 1; i <= this.getMaxLevel(); i++) {
            if (this.selects[i]) {
                this.selects[i].selectedIndex = 0;
                if (i > 1) this.selects[i].disabled = true;
                this.hideSpinner(i);
            }
        }
        this.updatePath();
    }
    
    // 선택된 값 반환
    getSelectedValues() {
        const values = {};
        for (let i = 1; i <= this.getMaxLevel(); i++) {
            const select = this.selects[i];
            if (select?.value) {
                values[`level${i}`] = {
                    id: select.value,
                    name: select.options[select.selectedIndex].textContent,
                    level: i
                };
            }
        }
        return values;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new CascadeSelector();
});
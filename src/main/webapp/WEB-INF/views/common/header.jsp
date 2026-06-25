<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="header-inner">
    <div class="header-left">
        <a class="brand" href="${pageContext.request.contextPath}/book/list">BOOK FOREST</a>

        <nav class="main-nav">
            <a href="${pageContext.request.contextPath}/book/list">도서목록</a>
            <a href="${pageContext.request.contextPath}/support/faq">자주 묻는 질문</a>
			<a href="${pageContext.request.contextPath}/qna/list">문의게시판</a>

			<c:if test="${sessionScope.loginUser ne 'admin' and sessionScope.loginUser ne 'tracking'}">
			    <a href="${pageContext.request.contextPath}/cart/list">장바구니</a>
			    <a href="${pageContext.request.contextPath}/order/list">주문내역</a>
			</c:if>

            <c:if test="${sessionScope.loginUser eq 'admin'}">
                <a href="${pageContext.request.contextPath}/admin/insertform">도서등록</a>
                <a href="${pageContext.request.contextPath}/admin/sales">판매통계</a>
                <a href="${pageContext.request.contextPath}/admin/members">고객관리</a>
                <a href="${pageContext.request.contextPath}/chat/admin">실시간상담</a>
            </c:if>

            <c:if test="${sessionScope.loginUser eq 'tracking'}">
                <a href="${pageContext.request.contextPath}/admin/traking">배송관리</a>
            </c:if>
        </nav>
    </div>

    <div class="header-right">
        <c:choose>
            <c:when test="${not empty sessionScope.loginUser}">
                <button type="button" class="user-chip" onclick="toggleProfileDrawer(true)">${sessionScope.loginNickname}님</button>
                <a class="header-btn outline" href="${pageContext.request.contextPath}/member/logout">로그아웃</a>
            </c:when>
            <c:otherwise>
                <a class="header-btn outline" href="${pageContext.request.contextPath}/member/login">로그인</a>
                <a class="header-btn solid" href="${pageContext.request.contextPath}/member/register">회원가입</a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<div class="header-search-wrap">
    <form id="headerSearchForm" class="header-search-form" autocomplete="off">
        <select class="header-search-select" aria-label="검색 구분">
            <option value="all">통합검색</option>
            <option value="title">도서명</option>
        </select>

        <div class="header-search-box">
            <input type="text"
                   id="headerSearchInput"
                   class="header-search-input"
                   name="keyword"
                   value="${param.keyword}"
                   placeholder="도서명을 검색하세요"
                   autocomplete="off">
            <button type="button" id="headerSearchClear" class="header-search-clear" aria-label="검색어 지우기">&times;</button>

            <div id="headerSearchPanel" class="header-search-panel">
                <div class="header-search-left">
                    <section id="recentSearchSection" class="header-search-section">
                        <div class="header-search-title">최근 검색어</div>
                        <div id="recentSearchList" class="header-search-list"></div>
                    </section>

                    <section id="autoCompleteSection" class="header-search-section">
                        <div class="header-search-title">자동 완성</div>
                        <div id="autoCompleteList" class="header-search-list"></div>
                    </section>
                </div>

                <section class="header-search-section header-popular-section">
                    <div class="header-search-title">연간 검색어</div>
                    <div id="popularSearchList" class="header-search-list"></div>
                </section>
            </div>
        </div>

        <button type="submit" class="header-search-submit" aria-label="검색">
            <i class="fa-solid fa-magnifying-glass"></i>
        </button>
    </form>
</div>

<script>
(function() {
    var contextPath = '${pageContext.request.contextPath}';
    var form = document.getElementById('headerSearchForm');
    var input = document.getElementById('headerSearchInput');
    var clearBtn = document.getElementById('headerSearchClear');
    var panel = document.getElementById('headerSearchPanel');
    var recentSection = document.getElementById('recentSearchSection');
    var autoSection = document.getElementById('autoCompleteSection');
    var recentList = document.getElementById('recentSearchList');
    var autoList = document.getElementById('autoCompleteList');
    var popularList = document.getElementById('popularSearchList');
    var storageKey = 'bookForestRecentSearches';
    var autoTimer = null;

    function getRecent() {
        try {
            var saved = localStorage.getItem(storageKey);
            return saved ? JSON.parse(saved) : [];
        } catch (e) {
            return [];
        }
    }

    function setRecent(list) {
        try {
            localStorage.setItem(storageKey, JSON.stringify(list.slice(0, 10)));
        } catch (e) {}
    }

    function saveRecent(keyword) {
        keyword = (keyword || '').trim();
        if (!keyword) return;

        var list = getRecent();
        var next = [keyword];
        for (var i = 0; i < list.length; i++) {
            if (list[i] !== keyword) next.push(list[i]);
        }
        setRecent(next);
    }

    function removeRecent(keyword) {
        var list = getRecent();
        var next = [];
        for (var i = 0; i < list.length; i++) {
            if (list[i] !== keyword) next.push(list[i]);
        }
        setRecent(next);
        renderRecent();
    }

    function goSearch(keyword) {
        keyword = (keyword || input.value || '').trim();
        if (!keyword) return;

        saveRecent(keyword);
        location.href = contextPath + '/book/search?keyword=' + encodeURIComponent(keyword);
    }

    function makeRow(keyword, removable, rank) {
        var row = document.createElement('div');
        row.className = 'header-search-row';

        var button = document.createElement('button');
        button.type = 'button';
        button.className = 'header-search-word';

        if (rank) {
            var rankSpan = document.createElement('span');
            rankSpan.className = 'header-search-rank';
            rankSpan.textContent = String(rank).length === 1 ? '0' + rank : String(rank);
            button.appendChild(rankSpan);
        }

        var textSpan = document.createElement('span');
        textSpan.className = 'header-search-text';
        textSpan.textContent = keyword;
        button.appendChild(textSpan);

        button.onclick = function() {
            input.value = keyword;
            goSearch(keyword);
        };

        row.appendChild(button);

        if (removable) {
            var del = document.createElement('button');
            del.type = 'button';
            del.className = 'header-search-delete';
            del.innerHTML = '&times;';
            del.setAttribute('aria-label', '최근 검색어 삭제');
            del.onclick = function(e) {
                e.stopPropagation();
                removeRecent(keyword);
            };
            row.appendChild(del);
        }

        return row;
    }

    function renderRecent() {
        var list = getRecent();
        recentList.innerHTML = '';

        if (list.length === 0) {
            recentList.innerHTML = '<div class="header-search-empty">최근 검색어가 없습니다.</div>';
        } else {
            for (var i = 0; i < list.length; i++) {
                recentList.appendChild(makeRow(list[i], true, null));
            }
        }
    }

    function renderAuto(list) {
        autoList.innerHTML = '';

        if (!input.value.trim()) {
            autoSection.style.display = 'none';
            recentSection.style.display = 'block';
            return;
        }

        autoSection.style.display = 'block';
        recentSection.style.display = 'none';

        if (!list || list.length === 0) {
            autoList.innerHTML = '<div class="header-search-empty">자동 완성 결과가 없습니다.</div>';
            return;
        }

        for (var i = 0; i < list.length; i++) {
            autoList.appendChild(makeRow(list[i], false, null));
        }
    }

    function renderPopular(list) {
        popularList.innerHTML = '';
        if (!list || list.length === 0) return;

        for (var i = 0; i < list.length; i++) {
            popularList.appendChild(makeRow(list[i], false, i + 1));
        }
    }

    function loadAuto(keyword) {
        fetch(contextPath + '/search/autocomplete?keyword=' + encodeURIComponent(keyword))
            .then(function(res) { return res.json(); })
            .then(function(list) { renderAuto(list); })
            .catch(function() { renderAuto([]); });
    }

    function loadPopular() {
        fetch(contextPath + '/search/popular')
            .then(function(res) { return res.json(); })
            .then(function(list) { renderPopular(list); })
            .catch(function() {});
    }

    function openPanel() {
        renderRecent();
        panel.classList.add('open');
        if (input.value.trim()) loadAuto(input.value.trim());
    }

    function closePanelDelay() {
        setTimeout(function() {
            panel.classList.remove('open');
        }, 160);
    }

    input.addEventListener('focus', openPanel);
    input.addEventListener('blur', closePanelDelay);

    input.addEventListener('input', function() {
        var keyword = input.value.trim();
        clearBtn.classList.toggle('show', keyword.length > 0);
        panel.classList.add('open');
        clearTimeout(autoTimer);

        if (!keyword) {
            renderAuto([]);
            return;
        }

        autoTimer = setTimeout(function() {
            loadAuto(keyword);
        }, 100);
    });

    clearBtn.addEventListener('click', function() {
        input.value = '';
        clearBtn.classList.remove('show');
        renderAuto([]);
        input.focus();
    });

    form.addEventListener('submit', function(e) {
        e.preventDefault();
        goSearch(input.value);
    });

    renderRecent();
    loadPopular();
    clearBtn.classList.toggle('show', input.value.trim().length > 0);
})();
</script>

<c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser ne 'admin'}">
    <div id="profileDrawerBackdrop" class="profile-drawer-backdrop" onclick="toggleProfileDrawer(false)"></div>
    <aside id="profileDrawer" class="profile-drawer">
        <div class="profile-drawer-top">
            <div>
                <div class="profile-drawer-label">MY MENU</div>
                <div class="profile-drawer-name">${sessionScope.loginUser}님</div>
                <div class="profile-drawer-sub">회원 정보와 계정 설정을 관리할 수 있어요.</div>
            </div>
            <button type="button" class="profile-close" onclick="toggleProfileDrawer(false)">&times;</button>
        </div>

        <div class="profile-drawer-menu">
            <a href="${pageContext.request.contextPath}/member/profile">정보 수정</a>
            <a href="${pageContext.request.contextPath}/member/password">비밀번호 수정</a>
            <a href="${pageContext.request.contextPath}/order/list">주문내역</a>
            <a href="${pageContext.request.contextPath}/cart/list">장바구니</a>
            <a href="${pageContext.request.contextPath}/member/delete">회원 탈퇴</a>
            <a href="${pageContext.request.contextPath}/member/logout">로그아웃</a>
        </div>
    </aside>

    <script>
        function toggleProfileDrawer(open) {
            var drawer = document.getElementById('profileDrawer');
            var backdrop = document.getElementById('profileDrawerBackdrop');
            if (!drawer || !backdrop) return;
            drawer.classList.toggle('open', open);
            backdrop.classList.toggle('show', open);
            document.body.classList.toggle('drawer-open', open);
        }
    </script>
</c:if>

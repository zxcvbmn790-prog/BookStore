<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>도서 등록</title>
    <link href="https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <style>
        :root {
            --bg:           #f5f0e8;
            --surface:      #faf7f2;
            --border:       #e2d9cc;
            --text:         #2c2520;
            --text-sub:     #8a7e74;
            --text-muted:   #b5aba0;
            --accent:       #5c4a3a;
            --accent-light: #e8ddd3;
        }

        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: 'DM Sans', sans-serif;
            background: var(--bg);
            color: var(--text);
            min-height: 100vh;
        }

        header {
            position: fixed;
            top: 0; left: 0; right: 0;
            z-index: 100;
            height: 60px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 52px;
            background: var(--bg);
            border-bottom: 1px solid var(--border);
        }

        .logo {
            font-family: 'DM Serif Display', serif;
            font-size: 18px;
            color: var(--accent);
        }

        nav a {
            font-size: 13px;
            color: var(--text-sub);
            text-decoration: none;
            margin-left: 28px;
            transition: color 0.2s;
        }

        nav a:hover { color: var(--text); }

        main {
            padding-top: 60px;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .form-wrap {
            width: 100%;
            max-width: 560px;
            padding: 52px;
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 4px;
            margin: 48px auto;
        }

        .form-tag {
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            color: var(--text-sub);
            background: var(--accent-light);
            padding: 4px 10px;
            border-radius: 2px;
            display: inline-block;
            margin-bottom: 20px;
        }

        .form-title {
            font-family: 'DM Serif Display', serif;
            font-size: 30px;
            color: var(--text);
            margin-bottom: 36px;
        }

        .field { margin-bottom: 20px; }

        label {
            display: block;
            font-size: 11px;
            font-weight: 500;
            letter-spacing: 1px;
            text-transform: uppercase;
            color: var(--text-muted);
            margin-bottom: 8px;
        }

        input {
            width: 100%;
            padding: 12px 14px;
            font-family: 'DM Sans', sans-serif;
            font-size: 14px;
            color: var(--text);
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: 4px;
            outline: none;
            transition: border-color 0.2s;
        }

        input:focus { border-color: var(--accent); }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 32px;
        }

        .btn {
            padding: 13px 22px;
            font-family: 'DM Sans', sans-serif;
            font-size: 13px;
            font-weight: 500;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            transition: all 0.2s;
        }

        .btn-cancel {
            background: var(--accent-light);
            color: var(--accent);
            border: 1px solid var(--border);
        }

        .btn-cancel:hover { background: var(--border); }

        .btn-submit {
            flex: 1;
            background: var(--accent);
            color: #faf7f2;
        }

        .btn-submit:hover { background: #3e3028; }
    </style>
</head>
<body>

<header>
    <div class="logo">Booklist</div>
    <nav>
        <a href="${pageContext.request.contextPath}/admin/list">전체 목록</a>
    </nav>
</header>

<main>
    <div class="form-wrap">
        <span class="form-tag">Admin</span>
        <div class="form-title">도서 등록</div>

        <form action="${pageContext.request.contextPath}/admin/insert" method="post">
            <div class="field">
                <label>ISBN</label>
                <input type="number" name="isbn" placeholder="ISBN 번호 입력" required>
            </div>
            <div class="field">
                <label>도서명</label>
                <input type="text" name="bookname" placeholder="도서명 입력" required>
            </div>
            <div class="field">
                <label>저자</label>
                <input type="text" name="author" placeholder="저자 입력" required>
            </div>
            <div class="field">
                <label>출판사</label>
                <input type="text" name="publisher" placeholder="출판사 입력" required>
            </div>
            <div class="field">
                <label>이미지 경로</label>
                <input type="text" name="image" placeholder="/images/book.jpg">
            </div>
            <div class="field">
                <label>가격</label>
                <input type="text" name="price" placeholder="가격 입력 (숫자만)">
            </div>

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/admin/list" class="btn btn-cancel">취소</a>
                <button type="submit" class="btn btn-submit">등록하기</button>
            </div>
        </form>
    </div>
</main>

</body>
</html>

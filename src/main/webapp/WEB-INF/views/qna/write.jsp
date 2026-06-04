<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
    .qna-form {
        display: flex;
        flex-direction: column;
        gap: 16px;
    }

    .qna-form-row {
        display: flex;
        flex-direction: column;
        gap: 8px;
    }

    .qna-form-row label {
        font-weight: 800;
        color: #111827;
    }

    .qna-input,
    .qna-textarea {
        width: 100%;
        border: 1px solid #d1d5db;
        border-radius: 14px;
        padding: 13px 14px;
        font-size: 15px;
        box-sizing: border-box;
    }

    .qna-textarea {
        min-height: 220px;
        resize: vertical;
        line-height: 1.7;
    }

    .qna-actions {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        margin-top: 8px;
    }

    .qna-btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-height: 42px;
        padding: 0 18px;
        border-radius: 999px;
        border: 1px solid #d1d5db;
        background: #fff;
        color: #111827;
        text-decoration: none;
        font-weight: 700;
        cursor: pointer;
    }

    .qna-btn.primary {
        border-color: #111827;
        background: #111827;
        color: #fff;
    }

    .qna-message.error {
        padding: 12px 14px;
        border-radius: 12px;
        margin-bottom: 16px;
        font-weight: 700;
        background: #fef2f2;
        color: #b91c1c;
        border: 1px solid #fecaca;
    }
</style>

<section class="stats-page">
    <div class="section-head">
        <span class="section-badge">WRITE</span>
        <h2>문의 작성</h2>
        <p>도서, 주문, 회원 정보와 관련된 문의를 작성해주세요.</p>
    </div>

    <div class="stats-panel">

        <c:if test="${not empty qnaError}">
            <div class="qna-message error">${qnaError}</div>
        </c:if>

        <form class="qna-form"
              action="${pageContext.request.contextPath}/qna/write"
              method="post">

            <div class="qna-form-row">
                <label for="subject">제목</label>
                <input id="subject"
                       class="qna-input"
                       type="text"
                       name="subject"
                       placeholder="문의 제목을 입력하세요."
                       required>
            </div>

            <div class="qna-form-row">
                <label for="content">내용</label>
                <textarea id="content"
                          class="qna-textarea"
                          name="content"
                          placeholder="문의 내용을 자세히 입력하세요."
                          required></textarea>
            </div>

            <div class="qna-actions">
                <a class="qna-btn" href="${pageContext.request.contextPath}/qna/list">
                    목록
                </a>
                <button class="qna-btn primary" type="submit">
                    등록
                </button>
            </div>
        </form>
    </div>
</section>
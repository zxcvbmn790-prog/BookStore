<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
    .qna-detail-head {
        border-bottom: 1px solid #e5e7eb;
        padding-bottom: 18px;
        margin-bottom: 22px;
    }

    .qna-detail-title {
        margin: 0 0 12px;
        font-size: 26px;
        color: #111827;
    }

    .qna-meta {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        color: #6b7280;
        font-size: 14px;
    }

    .qna-status {
        display: inline-flex;
        align-items: center;
        padding: 5px 10px;
        border-radius: 999px;
        font-size: 13px;
        font-weight: 700;
        background: #f3f4f6;
        color: #374151;
    }

    .qna-status.done {
        background: #eff6ff;
        color: #1d4ed8;
    }

    .qna-content {
        white-space: pre-line;
        line-height: 1.8;
        color: #374151;
        font-size: 16px;
        padding: 10px 0 26px;
        border-bottom: 1px solid #e5e7eb;
    }

    .qna-section-title {
        margin: 26px 0 14px;
        font-size: 20px;
    }

    .answer-box {
        border: 1px solid #e5e7eb;
        border-radius: 16px;
        padding: 16px;
        margin-bottom: 12px;
        background: #f9fafb;
    }

    .answer-top {
        display: flex;
        justify-content: space-between;
        gap: 12px;
        margin-bottom: 10px;
        color: #6b7280;
        font-size: 14px;
    }

    .answer-writer {
        font-weight: 800;
        color: #111827;
    }

    .answer-admin {
        color: #1d4ed8;
    }

    .answer-content {
        white-space: pre-line;
        line-height: 1.7;
        color: #374151;
    }

    .qna-empty {
        padding: 22px;
        text-align: center;
        color: #6b7280;
        background: #f9fafb;
        border-radius: 16px;
        border: 1px solid #e5e7eb;
    }

    .answer-form {
        display: flex;
        flex-direction: column;
        gap: 12px;
        margin-top: 14px;
    }

    .qna-textarea {
        width: 100%;
        min-height: 150px;
        border: 1px solid #d1d5db;
        border-radius: 14px;
        padding: 13px 14px;
        font-size: 15px;
        line-height: 1.7;
        resize: vertical;
        box-sizing: border-box;
    }

    .qna-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        gap: 10px;
        margin-top: 24px;
    }

    .qna-actions-left,
    .qna-actions-right {
        display: flex;
        gap: 10px;
        align-items: center;
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

    .qna-btn.danger {
        border-color: #fecaca;
        background: #fef2f2;
        color: #b91c1c;
    }

    .qna-message {
        padding: 12px 14px;
        border-radius: 12px;
        margin-bottom: 16px;
        font-weight: 700;
    }

    .qna-message.success {
        background: #ecfdf5;
        color: #047857;
        border: 1px solid #a7f3d0;
    }

    .qna-message.error {
        background: #fef2f2;
        color: #b91c1c;
        border: 1px solid #fecaca;
    }
</style>

<section class="stats-page">
    <div class="section-head">
        <span class="section-badge">Q&A</span>
        <h2>문의 상세</h2>
        <p>문의 내용과 관리자 답변을 확인할 수 있습니다.</p>
    </div>

    <div class="stats-panel">

        <c:if test="${not empty qnaMessage}">
            <div class="qna-message success">${qnaMessage}</div>
        </c:if>

        <c:if test="${not empty qnaError}">
            <div class="qna-message error">${qnaError}</div>
        </c:if>

        <div class="qna-detail-head">
            <h3 class="qna-detail-title">${question.subject}</h3>

            <div class="qna-meta">
                <span>작성자: ${question.userid}</span>
                <span>
                    작성일:
                    <fmt:formatDate value="${question.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                </span>

                <c:choose>
                    <c:when test="${question.status eq '답변완료'}">
                        <span class="qna-status done">${question.status}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="qna-status">${question.status}</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>

        <div class="qna-content">${question.content}</div>

        <h3 class="qna-section-title">답변</h3>

        <c:choose>
            <c:when test="${empty question.answerList}">
                <div class="qna-empty">
                    아직 등록된 답변이 없습니다.
                </div>
            </c:when>

            <c:otherwise>
                <c:forEach var="answer" items="${question.answerList}">
                    <div class="answer-box">
                        <div class="answer-top">
                            <span class="answer-writer ${answer.admin ? 'answer-admin' : ''}">
                                <c:choose>
                                    <c:when test="${answer.admin}">
                                        관리자
                                    </c:when>
                                    <c:otherwise>
                                        ${answer.writer}
                                    </c:otherwise>
                                </c:choose>
                            </span>

                            <span>
                                <fmt:formatDate value="${answer.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                            </span>
                        </div>

                        <div class="answer-content">${answer.content}</div>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <c:if test="${sessionScope.loginUser eq 'admin'}">
            <h3 class="qna-section-title">관리자 답변 작성</h3>

            <form class="answer-form"
                  action="${pageContext.request.contextPath}/qna/answer"
                  method="post">

                <input type="hidden" name="questionId" value="${question.questionId}">

                <textarea class="qna-textarea"
                          name="content"
                          placeholder="답변 내용을 입력하세요."
                          required></textarea>

                <div style="display:flex; justify-content:flex-end;">
                    <button class="qna-btn primary" type="submit">
                        답변 등록
                    </button>
                </div>
            </form>
        </c:if>

        <div class="qna-actions">
            <div class="qna-actions-left">
                <a class="qna-btn" href="${pageContext.request.contextPath}/qna/list">
                    목록
                </a>
            </div>

            <div class="qna-actions-right">
                <c:if test="${sessionScope.loginUser eq question.userid or sessionScope.loginUser eq 'admin'}">
                    <form action="${pageContext.request.contextPath}/qna/delete"
                          method="post"
                          onsubmit="return confirm('문의를 삭제하시겠습니까?');">

                        <input type="hidden" name="questionId" value="${question.questionId}">
                        <button class="qna-btn danger" type="submit">
                            삭제
                        </button>
                    </form>
                </c:if>
            </div>
        </div>
    </div>
</section>
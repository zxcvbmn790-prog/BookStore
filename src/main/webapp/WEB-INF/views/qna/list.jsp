<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<style>
    .qna-top-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 18px;
        gap: 12px;
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

    .qna-btn {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-height: 40px;
        padding: 0 16px;
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

    .qna-title-link {
        color: #111827;
        text-decoration: none;
        font-weight: 700;
    }

    .qna-title-link:hover {
        text-decoration: underline;
    }

    .qna-status {
        display: inline-flex;
        align-items: center;
        padding: 6px 10px;
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

    .qna-empty {
        padding: 40px 0;
        text-align: center;
        color: #6b7280;
    }
</style>

<section class="stats-page">
    <div class="section-head">
        <span class="section-badge">Q&A</span>
        <h2>문의게시판</h2>
        <p>도서, 주문, 회원 정보와 관련된 문의를 남길 수 있습니다.</p>
    </div>

    <div class="stats-panel">

        <c:if test="${not empty qnaMessage}">
            <div class="qna-message success">${qnaMessage}</div>
        </c:if>

        <c:if test="${not empty qnaError}">
            <div class="qna-message error">${qnaError}</div>
        </c:if>

        <div class="qna-top-actions">
            <h3>문의 목록</h3>

            <c:if test="${not empty sessionScope.loginUser and sessionScope.loginUser ne 'admin'}">
                <a class="qna-btn primary" href="${pageContext.request.contextPath}/qna/write">
                    문의 작성
                </a>
            </c:if>
        </div>

        <table class="stats-table">
            <thead>
                <tr>
                    <th style="width:80px;">번호</th>
                    <th>제목</th>
                    <th style="width:140px;">작성자</th>
                    <th style="width:120px;">상태</th>
                    <th style="width:180px;">작성일</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty qnaList}">
                        <tr>
                            <td colspan="5">
                                <div class="qna-empty">
                                    등록된 문의가 없습니다.
                                </div>
                            </td>
                        </tr>
                    </c:when>

                    <c:otherwise>
                        <c:forEach var="qna" items="${qnaList}">
                            <tr>
                                <td>${qna.questionId}</td>
                                <td style="text-align:left;">
                                    <a class="qna-title-link"
                                       href="${pageContext.request.contextPath}/qna/view?questionId=${qna.questionId}">
                                        ${qna.subject}
                                    </a>
                                </td>
                                <td>${qna.userid}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${qna.status eq '답변완료'}">
                                            <span class="qna-status done">${qna.status}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="qna-status">${qna.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <fmt:formatDate value="${qna.createdAt}" pattern="yyyy-MM-dd HH:mm"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
</section>
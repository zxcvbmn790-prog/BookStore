<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
.btn-inline {
    flex-shrink: 0;
    padding: 0 16px;
    background: #6366f1;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    height: 44px;
}
.btn-inline:hover { background: #4f46e5; }
.btn-inline:disabled { background: #9ca3af; cursor: not-allowed; }

.otp-section {
    display: none;
    margin-top: 10px;
    padding: 16px;
    background: #f9fafb;
    border-radius: 10px;
    border: 1px solid #e5e7eb;
}
.otp-section.show { display: block; }

.otp-row {
    display: flex;
    gap: 8px;
    align-items: center;
}
.otp-input {
    flex: 1;
    text-align: center;
    font-size: 20px;
    font-weight: 700;
    letter-spacing: 8px;
    padding: 10px 4px;
    border: 2px solid #e5e7eb;
    border-radius: 8px;
    outline: none;
}
.otp-input:focus { border-color: #6366f1; }

.otp-timer {
    font-size: 15px;
    font-weight: 700;
    color: #6366f1;
    min-width: 42px;
    text-align: center;
}
.otp-timer.expired { color: #ef4444; }

.otp-msg { display: block; margin-top: 8px; font-size: 12px; }
.otp-msg.error { color: #b91c1c; }
.otp-msg.success { color: #166534; }

.verified-badge {
    display: none;
    align-items: center;
    gap: 6px;
    margin-top: 6px;
    padding: 6px 12px;
    background: #ecfdf3;
    color: #166534;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 600;
}
.verified-badge.show { display: inline-flex; }

.pw-divider {
    border: none;
    border-top: 1px solid #e5e7eb;
    margin: 24px 0 20px;
}
.pw-match-ok  { font-size: 12px; color: #166534; margin-top: 4px; }
.pw-match-err { font-size: 12px; color: #b91c1c; margin-top: 4px; }
.form-submit:disabled { background: #d1d5db; cursor: not-allowed; }
</style>

<section class="account-shell">
    <div class="account-card">
        <div class="auth-top">
            <span class="section-badge">PASSWORD</span>
            <h2>비밀번호 수정</h2>
            <p>이메일 인증 후 비밀번호를 변경할 수 있습니다.</p>
        </div>

        <c:if test="${not empty passwordError}">
            <div class="auth-alert">${passwordError}</div>
        </c:if>

        <!-- Step 1: 이메일 OTP 인증 -->
        <div id="otpStep">
            <div class="form-field">
                <label>본인 확인 이메일</label>
                <div style="display:flex; gap:8px;">
                    <input type="email" id="otpEmail" value="${member.email}" readonly
                           style="flex:1; background:#f3f4f6; cursor:default;">
                    <button type="button" id="btnSendOtp" class="btn-inline" onclick="sendOtp()">인증번호 발송</button>
                </div>
                <span id="verifiedBadge" class="verified-badge">
                    <i class="fas fa-check-circle"></i> 본인 인증 완료
                </span>
            </div>

            <div id="otpSection" class="otp-section">
                <div class="otp-row">
                    <input type="text" id="otpInput" class="otp-input" maxlength="6"
                           placeholder="------" inputmode="numeric" pattern="[0-9]*">
                    <span class="otp-timer" id="otpTimer"></span>
                    <button type="button" id="btnVerifyOtp" class="btn-inline"
                            onclick="verifyOtp()" style="padding:0 20px;">확인</button>
                </div>
                <small id="otpMsg" class="otp-msg"></small>
            </div>
        </div>

        <!-- Step 2: 비밀번호 변경 (OTP 인증 후 표시) -->
        <div id="pwStep" style="display:none">
            <hr class="pw-divider">
            <form id="passwordForm"
                  action="${pageContext.request.contextPath}/member/password/update"
                  method="post" class="auth-form" onsubmit="return validatePw()">
                <div class="form-field">
                    <label for="currentPassword">현재 비밀번호</label>
                    <input type="password" id="currentPassword" name="currentPassword" required>
                </div>
                <div class="form-field">
                    <label for="newPassword">새 비밀번호</label>
                    <input type="password" id="newPassword" name="newPassword"
                           required oninput="checkMatch()">
                </div>
                <div class="form-field">
                    <label for="confirmPassword">새 비밀번호 확인</label>
                    <input type="password" id="confirmPassword" name="confirmPassword"
                           required oninput="checkMatch()">
                    <small id="pwMatchMsg"></small>
                </div>
                <button type="submit" id="btnSubmit" class="form-submit" disabled>비밀번호 변경</button>
            </form>
        </div>
    </div>
</section>

<script>
var ctx = '${pageContext.request.contextPath}';
var timerInterval;
var remaining = 0;

function sendOtp() {
    var email = document.getElementById('otpEmail').value.trim();
    var btn = document.getElementById('btnSendOtp');
    btn.disabled = true;
    btn.textContent = '발송 중...';

    fetch(ctx + '/member/sendOtp', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'email=' + encodeURIComponent(email)
    })
    .then(function(r) { return r.json(); })
    .then(function(res) {
        showOtpMsg(res.message, 'success');
        remaining = res.remainingSec || 180;
        startTimer();
        document.getElementById('otpSection').classList.add('show');
        document.getElementById('otpInput').value = '';
        document.getElementById('otpInput').focus();
        document.getElementById('btnVerifyOtp').disabled = false;
        btn.disabled = false;
        btn.textContent = '재발송';
    })
    .catch(function() {
        alert('인증번호 발송에 실패했습니다.');
        btn.disabled = false;
        btn.textContent = '인증번호 발송';
    });
}

function verifyOtp() {
    var otp = document.getElementById('otpInput').value.trim();
    if (otp.length !== 6) {
        showOtpMsg('6자리 인증번호를 입력해주세요.', 'error');
        return;
    }
    var btn = document.getElementById('btnVerifyOtp');
    btn.disabled = true;
    btn.textContent = '확인 중...';

    fetch(ctx + '/member/verifyOtp', {
        method: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        body: 'otp=' + encodeURIComponent(otp)
    })
    .then(function(r) {
        return r.json().then(function(data) {
            return { ok: r.ok, data: data };
        });
    })
    .then(function(result) {
        if (result.ok) {
            clearInterval(timerInterval);
            document.getElementById('otpSection').classList.remove('show');
            document.getElementById('verifiedBadge').classList.add('show');
            document.getElementById('btnSendOtp').style.display = 'none';
            document.getElementById('pwStep').style.display = 'block';
            showOtpMsg('', '');
        } else {
            var res = result.data;
            if (res && res.result === 'expired') {
                showOtpMsg(res.message, 'error');
                clearInterval(timerInterval);
                remaining = 0;
                updateTimerDisplay();
                document.getElementById('btnVerifyOtp').disabled = true;
                document.getElementById('btnVerifyOtp').textContent = '확인';
            } else {
                showOtpMsg(res ? res.message : '인증번호가 일치하지 않습니다.', 'error');
                btn.disabled = false;
                btn.textContent = '확인';
            }
        }
    })
    .catch(function() {
        showOtpMsg('오류가 발생했습니다.', 'error');
        btn.disabled = false;
        btn.textContent = '확인';
    });
}

function checkMatch() {
    var np = document.getElementById('newPassword').value;
    var cp = document.getElementById('confirmPassword').value;
    var msg = document.getElementById('pwMatchMsg');
    var btn = document.getElementById('btnSubmit');
    if (!cp) { msg.textContent = ''; btn.disabled = true; return; }
    if (np === cp) {
        msg.className = 'pw-match-ok';
        msg.textContent = '비밀번호가 일치합니다.';
        btn.disabled = false;
    } else {
        msg.className = 'pw-match-err';
        msg.textContent = '비밀번호가 일치하지 않습니다.';
        btn.disabled = true;
    }
}

function validatePw() {
    var np = document.getElementById('newPassword').value;
    var cp = document.getElementById('confirmPassword').value;
    if (np !== cp) {
        alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
        return false;
    }
    return true;
}

function startTimer() {
    clearInterval(timerInterval);
    updateTimerDisplay();
    timerInterval = setInterval(function() {
        remaining--;
        updateTimerDisplay();
        if (remaining <= 0) {
            clearInterval(timerInterval);
            document.getElementById('btnVerifyOtp').disabled = true;
        }
    }, 1000);
}

function updateTimerDisplay() {
    var el = document.getElementById('otpTimer');
    if (remaining <= 0) {
        el.textContent = '만료';
        el.classList.add('expired');
    } else {
        var m = Math.floor(remaining / 60);
        var s = remaining % 60;
        el.textContent = m + ':' + (s < 10 ? '0' : '') + s;
        el.classList.remove('expired');
    }
}

function showOtpMsg(msg, type) {
    var el = document.getElementById('otpMsg');
    el.textContent = msg;
    el.className = 'otp-msg' + (type ? ' ' + type : '');
}

// OTP 입력란 숫자만
document.getElementById('otpInput').addEventListener('input', function() {
    this.value = this.value.replace(/[^0-9]/g, '');
});
document.getElementById('otpInput').addEventListener('keyup', function(e) {
    if (e.key === 'Enter' && this.value.length === 6) verifyOtp();
});
</script>

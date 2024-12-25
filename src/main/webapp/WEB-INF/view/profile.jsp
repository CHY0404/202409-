<%@ page language="java" isErrorPage="true" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="zh-TW">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>會員資料</title>

    <!-- CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css">

</head>

<body>
    <!-- 上方工具欄 -->
    <%@ include file="/WEB-INF/view/fragment/header.jspf" %>

    <div class="d-flex">
        <!-- 左側選單 -->
        <%@ include file="/WEB-INF/view/fragment/sidebar.jspf" %>

        <!-- 主內容區域 -->
        <main id="main-content" class="main-content flex-grow-1 p-4">
            <div class="container-fluid">
                <div class="row justify-content-center">
                    <div class="col-lg-8 col-md-10 col-sm-12">
                        <h2 class="mb-4">會員資料</h2>

                        <!-- 錯誤或成功訊息 -->
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger" role="alert">${error}</div>
                        </c:if>
                        <c:if test="${not empty success}">
                            <div class="alert alert-success" role="alert">${success}</div>
                        </c:if>

                        <form method="post" action="/profile/update" class="row g-3">
                            <!-- 用戶名 -->
                            <div class="col-md-6">
                                <label for="username" class="form-label">用戶名</label>
                                <input type="text" id="username" name="username" class="form-control" value="${user.username}" required>
                            </div>

                            <!-- 電子郵件 -->
                            <div class="col-md-6">
                                <label for="email" class="form-label">電子郵件</label>
                                <input type="email" id="email" name="email" class="form-control" value="${user.email}" required>
                            </div>

                            <!-- 修改密碼 -->
                            <div class="col-12 mt-4">
                                <h4>變更密碼</h4>
                            </div>

                            <div class="col-md-6">
                                <label for="currentPassword" class="form-label">當前密碼</label>
                                <div class="input-group">
                                    <input type="password" id="currentPassword" name="currentPassword" class="form-control" required>
                                    <button type="button" class="btn btn-outline-secondary toggle-password">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <label for="newPassword" class="form-label">新密碼</label>
                                <div class="input-group">
                                    <input type="password" id="newPassword" name="newPassword" class="form-control" required>
                                    <button type="button" class="btn btn-outline-secondary toggle-password">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <div class="col-md-6">
                                <label for="confirmPassword" class="form-label">確認新密碼</label>
                                <div class="input-group">
                                    <input type="password" id="confirmPassword" name="confirmPassword" class="form-control" required>
                                    <button type="button" class="btn btn-outline-secondary toggle-password">
                                        <i class="bi bi-eye"></i>
                                    </button>
                                </div>
                            </div>

                            <!-- 提交按鈕 -->
                            <div class="col-12 mt-4">
                                <button type="submit" class="btn btn-primary">更新資料</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </main>
    </div>

    <!-- 顯示/隱藏密碼的 JavaScript -->
    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const toggleButtons = document.querySelectorAll(".toggle-password");
            toggleButtons.forEach(button => {
                button.addEventListener("click", function () {
                    const input = this.previousElementSibling;
                    const icon = this.querySelector("i");
                    if (input.type === "password") {
                        input.type = "text";
                        icon.classList.remove("bi-eye");
                        icon.classList.add("bi-eye-slash");
                    } else {
                        input.type = "password";
                        icon.classList.remove("bi-eye-slash");
                        icon.classList.add("bi-eye");
                    }
                });
            });
        });
    </script>

    <script src="${pageContext.request.contextPath}/js/menu.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

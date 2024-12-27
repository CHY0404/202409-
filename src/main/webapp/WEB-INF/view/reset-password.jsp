<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>重置密碼</title>

    <!-- 引入 Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hidden {
            display: none;
        }
    </style>
</head>

<body>
    <div class="container py-5">
        <!-- 使用 Bootstrap 卡片框住輸入欄位 -->
        <div class="row justify-content-center">
            <div class="col-12 col-md-8 col-lg-6">
                <div class="card shadow">
                    <div class="card-header text-center bg-primary text-white">
                        <h5>重置密碼</h5>
                    </div>
                    <div class="card-body">
                        <!-- 顯示錯誤訊息 -->
                        <div class="alert alert-danger ${empty error ? 'hidden' : ''}" role="alert">
                            ${error}
                        </div>

                        <!-- 顯示成功訊息 -->
                        <div class="alert alert-success ${empty success ? 'hidden' : ''}" role="alert">
                            ${success}
                        </div>

                        <form method="post" action="/password/reset">
                            <div class="mb-3">
                                <label for="email" class="form-label">電子郵件</label>
                                <input type="email" id="email" name="email" class="form-control" placeholder="輸入電子郵件" required>
                            </div>
                            <div class="mb-3">
                                <label for="verificationCode" class="form-label">驗證碼</label>
                                <input type="text" id="verificationCode" name="verificationCode" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label for="newPassword" class="form-label">新密碼</label>
                                <input type="password" id="newPassword" name="newPassword" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">重置密碼</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 引入 Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html>
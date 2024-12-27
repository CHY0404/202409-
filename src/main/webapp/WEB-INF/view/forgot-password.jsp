<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-TW">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>忘記密碼</title>

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
                        <h5>忘記密碼</h5>
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

                        <form method="post" action="/password/send-code">
                            <div class="mb-3">
                                <label for="username" class="form-label">用戶名</label>
                                <input type="text" id="username" name="username" class="form-control" placeholder="輸入用戶名" required>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">電子郵件</label>
                                <input type="email" id="email" name="email" class="form-control" placeholder="輸入電子郵件" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">發送驗證碼</button>
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


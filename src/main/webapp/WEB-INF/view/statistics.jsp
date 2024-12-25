<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-TW">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>收支統計</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/menu.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<body>
    <%-- 上方工具欄 --%>
    <%@ include file="/WEB-INF/view/fragment/header.jspf" %>

    <div class="d-flex">
        <%-- 左側選單 --%>
        <%@ include file="/WEB-INF/view/fragment/sidebar.jspf" %>

        <main id="main-content" class="main-content flex-grow-1 p-4">
            <div class="container-fluid">
                <div class="row justify-content-center">
                    <div class="col-lg-9 col-md-10 col-sm-12">
                        <h2 class="text-center">收支管家</h2>

                        <div class="container my-4">
                            <!-- 統計範圍選擇 -->
                            <div class="mb-3">
                                <button type="button" class="btn btn-outline-primary me-2 mb-2" id="last7Days">近 7 天</button>
                                <button type="button" class="btn btn-outline-primary me-2 mb-2" id="last30Days">近 30 天</button>
                                <button type="button" class="btn btn-outline-primary mb-2" id="yearly">年度統計</button>
                            </div>

                            <!-- 數值統計卡片 -->
                            <div class="row justify-content-center mb-4">
                                <!-- 總收入卡片 -->
                                <div class="col-lg-4 col-md-6 col-sm-12">
                                    <div class="card stat-card text-center">
                                        <div class="card-body">
                                            <h5 class="card-title mt-2">
                                                <i class="bi bi-arrow-up-circle text-success" style="font-size: 2rem;"></i> 總收入
                                            </h5>
                                            <p class="card-text">
                                                <span id="totalIncome">0</span> 元
                                            </p>
                                        </div>
                                    </div>
                                </div>

                                <!-- 總支出卡片 -->
                                <div class="col-lg-4 col-md-6 col-sm-12">
                                    <div class="card stat-card text-center">
                                        <div class="card-body">
                                            <h5 class="card-title mt-2">
                                                <i class="bi bi-arrow-down-circle text-danger" style="font-size: 2rem;"></i> 總支出
                                            </h5>
                                            <p class="card-text">
                                                <span id="totalExpense">0</span> 元
                                            </p>
                                        </div>
                                    </div>
                                </div>

                                <!-- 餘額卡片 -->
                                <div class="col-lg-4 col-md-6 col-sm-12">
                                    <div class="card stat-card text-center">
                                        <div class="card-body">
                                            <h5 class="card-title mt-2">
                                                <i class="bi bi-wallet2 text-primary" style="font-size: 2rem;"></i> 結餘
                                            </h5>
                                            <p class="card-text">
                                                <span id="balance">0</span> 元
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- 收支的柱狀圖 -->
                            <div class="card mb-4">
                                <div class="card-body">
                                    <h5 class="card-title">收支統計</h5>
                                    <canvas id="incomeExpenseBarChart" style="max-height: 400px;"></canvas>
                                </div>
                            </div>

                            <!-- 結餘的折線圖 -->
                            <div class="card">
                                <div class="card-body">
                                    <h5 class="card-title">結餘趨勢</h5>
                                    <canvas id="balanceLineChart" style="max-height: 400px;"></canvas>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </main>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="${pageContext.request.contextPath}/js/statistics.js"></script>
    <script src="${pageContext.request.contextPath}/js/menu.js"></script>
</body>

</html>

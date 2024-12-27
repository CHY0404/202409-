$(document).ready(function() {
    const pageContext = "http://localhost:8080";

    // 用於保存 Chart.js 實例
    let incomeExpenseChart;

    // 獲取統計數據
    async function fetchFinancialSummary() {
        try {
            const response = await fetch(`${pageContext}/wealth/summary`);
            const summary = await response.json();

            document.getElementById('totalIncome').textContent = summary.totalIncome;
            document.getElementById('totalExpense').textContent = summary.totalExpense;
            document.getElementById('balance').textContent = summary.balance;
        } catch (error) {
            console.error('無法獲取統計數據:', error);
        }
    }

// 渲染或更新圓餅圖
async function renderIncomeExpenseChart(startDate, endDate) {
    try {
        // 構建帶日期範圍的 API 請求 URL
        let url = `${pageContext}/wealth/summary`;
        if (startDate && endDate) {
            url += `?startDate=${startDate}&endDate=${endDate}`;
        }

        const response = await fetch(url);
        const summary = await response.json();

        const ctx = document.getElementById('incomeExpenseChart').getContext('2d');
        const totalAmount = summary.totalIncome + summary.totalExpense;

        // 防止 totalAmount 為 0 時導致計算錯誤
        const incomePercentage = totalAmount > 0 ? Math.round((summary.totalIncome / totalAmount) * 100) : 0;
        const expensePercentage = totalAmount > 0 ? 100 - incomePercentage : 0;

        if (incomeExpenseChart) {
            // 更新現有圖表數據
            incomeExpenseChart.data.datasets[0].data = [summary.totalIncome, summary.totalExpense];
            incomeExpenseChart.options.plugins.datalabels.formatter = function (value, context) {
                return context.dataIndex === 0
                    ? `${incomePercentage}%`
                    : `${expensePercentage}%`;
            };
            incomeExpenseChart.update();
        } else {
            // 創建新圖表
            incomeExpenseChart = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: ['收入', '支出'],
                    datasets: [{
                        data: [summary.totalIncome, summary.totalExpense],
                        backgroundColor: ['#36A2EB', '#FF6384'],
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        datalabels: {
                            formatter: function (value, context) {
                                return context.dataIndex === 0
                                    ? `${incomePercentage}%`
                                    : `${expensePercentage}%`;
                            },
                            color: '#fff',
                            font: {
                                weight: 'bold'
                            }
                        }
                    }
                },
                plugins: [ChartDataLabels]
            });
        }

        // 更新總收入、總支出和餘額
        document.getElementById('totalIncome').textContent = summary.totalIncome.toLocaleString('zh-TW', { maximumFractionDigits: 0 });
        document.getElementById('totalExpense').textContent = summary.totalExpense.toLocaleString('zh-TW', { maximumFractionDigits: 0 });
        document.getElementById('balance').textContent = summary.balance.toLocaleString('zh-TW', { maximumFractionDigits: 0 });
    } catch (error) {
        console.error('無法加載圓餅圖數據:', error);
    }
}

    // 初始化統計功能
    function initializeStatistics() {
        fetchFinancialSummary();
        renderIncomeExpenseChart();
    }

// 初始化 DataTables
const recordsTable = $('#recordsTable').DataTable({
    processing: true,
    serverSide: true,
    ajax: {
        url: `${pageContext}/wealth/records/json`,
        type: 'GET',
        data: function(d) {
            const startDate = $('#start-date').val();
            const endDate = $('#end-date').val();

            const orderColumn = d.order && d.order[0] ? d.order[0].column : 0;
            const orderDir = d.order && d.order[0] ? d.order[0].dir : 'desc';

            return {
                draw: d.draw,
                start: d.start,
                length: d.length,
                "search[value]": d.search ? d.search.value : "",
                "order[0][column]": orderColumn,
                "order[0][dir]": orderDir,
                startDate: startDate || null, // 自定義開始日期
                endDate: endDate || null      // 自定義結束日期
            };
        }
    },
    columns: [
        {
            data: 'timestamp',
            render: function(data) {
                const date = new Date(data);
                return date.toLocaleDateString('zh-HANT', {
                    year: 'numeric',
                    month: '2-digit',
                    day: '2-digit'
                });
            }
        },
        {
            data: 'type',
            render: function(data) {
                return `<span class="${data === 'INCOME' ? 'income' : 'expense'}">
                    ${data === 'INCOME' ? '收入' : '支出'}</span>`;
            }
        },
        { data: 'amount',
          render: function(data) {
          return data.toLocaleString('zh-TW', { maximumFractionDigits: 0 });
          }
         },
        { data: 'note', defaultContent: '' },
        {
            data: 'id',
            orderable: false,
            searchable: false,
            render: function(data) {
                return `
                    <button class="btn btn-outline-primary edit-record" data-id="${data}">編輯</button>
                    <button class="btn btn-outline-danger delete-record" data-id="${data}">刪除</button>
                `;
            }
        }
    ],
    order: [[0, 'desc']],
    language: {
        url: "https://cdn.datatables.net/plug-ins/1.13.4/i18n/zh-HANT.json"
    },
    pageLength: 10,
    lengthMenu: [[10, 25, 50], [10, 25, 50]]
});

// 在 DataTables 的 draw 事件中更新圖表
recordsTable.on('draw', function () {
    const startDate = $('#start-date').val();
    const endDate = $('#end-date').val();

    // 更新統計圖表
    renderIncomeExpenseChart(startDate, endDate);
});

// 初始化日期選擇器
flatpickr(".datepicker", {
    dateFormat: "Y-m-d"
});

// 篩選按鈕邏輯
$('#filter-btn').on('click', function() {
    const startDate = $('#start-date').val();
    const endDate = $('#end-date').val();

    if (!startDate || !endDate) {
        alert('請選擇開始和結束日期！');
        return;
    }

    recordsTable.ajax.reload(); // 重新加載表格數據
});

// 清空篩選邏輯
$('#clear-btn').on('click', function() {
    // 清空日期輸入框的值
    $('#start-date').val('');
    $('#end-date').val('');

    // 重新加載表格，恢復到初始狀態
    recordsTable.ajax.reload();
});

// 匯出按鈕邏輯
$('#export-btn').on('click', function() {
    const startDate = $('#start-date').val();
    const endDate = $('#end-date').val();

    if (!startDate || !endDate) {
        alert('請選擇開始和結束日期！');
        return;
    }

    const url = `${pageContext}/wealth/records/export-excel?startDate=${startDate}&endDate=${endDate}`;
    window.location.href = url;
});

    // 提交新增表單
    $('#recordForm').on('submit', function(e) {
        e.preventDefault();
        var formData = {
            amount: $('#amount').val(),
            type: $('input[name="type"]:checked').val(),
            timestamp: new Date().toISOString().slice(0, 16), // 使用当前时间
            note: $('#note').val()
        };

        $.ajax({
            url: `${pageContext}/wealth/records`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                recordsTable.ajax.reload(); // 重新加載表格數據
                $('#amount').val('');
                $('#note').val('');

                initializeStatistics();
            },
            error: function(xhr, status, error) {
                console.error('錯誤:', error);
            }
        });
    });

    // 刪除記錄
    $(document).on('click', '.delete-record', function() {
        var recordId = $(this).data('id');
        if (confirm('確定要刪除這條記錄嗎？')) {
            $.ajax({
                url: `${pageContext}/wealth/records/${recordId}`,
                type: 'DELETE',
                success: function(response) {
                    recordsTable.ajax.reload(); // 重新加載表格數據
                    initializeStatistics();
                },
                error: function(xhr, status, error) {
                    console.error('錯誤:', error);
                }
            });
        }
    });

    // 用於儲存觸發編輯操作的按鈕
    let editTriggerButton = null;

    // 編輯記錄
    $(document).on('click', '.edit-record', function() {
        editTriggerButton = $(this);

        var recordId = $(this).data('id');
        $('#editModal').modal('show');

        // 加載記錄詳情
        $.ajax({
            url: `${pageContext}/wealth/records/${recordId}`,
            type: 'GET',
            success: function(record) {
                $('#editId').val(record.id);
                $(`#edit${record.type}`).prop('checked', true); // 設定類別選項
                $('#editAmount').val(record.amount);
                $('#editNote').val(record.note);
            },
            error: function(xhr, status, error) {
                console.error('錯誤:', error);
            }
        });
    });

    // 提交編輯表單
    $('#editForm').on('submit', function(e) {
        e.preventDefault();
        var formData = {
            id: $('#editId').val(),
            type: $('input[name="editType"]:checked').val(), // 更新類別
            amount: $('#editAmount').val(),
            note: $('#editNote').val()
        };

        $.ajax({
            url: `${pageContext}/wealth/records/${formData.id}`,
            type: 'PUT',
            contentType: 'application/json',
            data: JSON.stringify(formData),
            success: function(response) {
                recordsTable.ajax.reload(); // 重新載入表格數據
                $('#editModal').modal('hide');
                initializeStatistics();

                if (editTriggerButton) {
                    editTriggerButton.focus();
                    editTriggerButton = null;
                }
            },
            error: function(xhr, status, error) {
                console.error('錯誤:', error);
            }
        });
    });

    // 表單驗證
    $('#recordForm').on('submit', function(event) {
        var amount = parseFloat($('#amount').val());
        if (amount < 0) {
            alert('金額不可為負數'); // 彈出提示框
            event.preventDefault(); // 阻止表单提交
        } else {
            $('#amountError').hide();
            $('#amount').removeClass('is-invalid');
        }
    });

    $('#editForm').on('submit', function(event) {
        var editAmount = parseFloat($('#editAmount').val());
        if (editAmount < 0) {
            $('#editAmountError').text('金額不可為負數').show();
            $('#editAmount').addClass('is-invalid');
            event.preventDefault(); // 阻止表单提交
        } else {
            $('#editAmountError').hide();
            $('#editAmount').removeClass('is-invalid');
        }
    });

    // 初始化統計數據與圓餅圖
    initializeStatistics();
});

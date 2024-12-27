document.addEventListener('DOMContentLoaded', function () {
    console.log('DOMContentLoaded 事件已觸發');
    const pageContext = "http://localhost:8080";
    let incomeExpenseBarChart;
    let balanceLineChart;

    // 初始化圖表數據
    async function fetchChartData(endpoint) {
        try {
            const response = await fetch(`${pageContext}/stats/api/${endpoint}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const data = await response.json();
            updateBarChart(data, endpoint);
            updateBalanceLineChart(data);
            updateSummaryCards(data);

        } catch (error) {
            console.error('無法獲取圖表數據:', error);
        }
    }

    function updateBarChart(data, endpoint) {
        const ctx = document.getElementById('incomeExpenseBarChart').getContext('2d');

        // 確保只聲明一次 labels
        let sortedLabels = Object.keys(data).sort((a, b) => {
            if (endpoint === 'yearly') {
                // 如果是年度統計，排序月份（1-12）
                return parseInt(a) - parseInt(b);
            } else {
                // 日期排序
                return new Date(a) - new Date(b);
            }
        });

        const incomeData = sortedLabels.map((label) => data[label]?.income || 0);
        const expenseData = sortedLabels.map((label) => data[label]?.expense || 0);

        if (incomeExpenseBarChart) {
            incomeExpenseBarChart.data.labels = sortedLabels; // 使用排序後的 labels
            incomeExpenseBarChart.data.datasets[0].data = incomeData;
            incomeExpenseBarChart.data.datasets[1].data = expenseData;
            incomeExpenseBarChart.update();
        } else {
            incomeExpenseBarChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: sortedLabels, // 使用排序後的 labels
                    datasets: [
                        {
                            label: '收入',
                            data: incomeData,
                            backgroundColor: '#36A2EB',
                        },
                        {
                            label: '支出',
                            data: expenseData,
                            backgroundColor: '#FF6384',
                        },
                    ],
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        tooltip: {
                            callbacks: {
                                label: function (context) {
                                    return `${context.dataset.label}: ${context.raw} 元`;
                                },
                            },
                        },
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: endpoint === 'yearly' ? '月份' : '日期',
                            },
                        },
                        y: {
                            title: {
                                display: true,
                                text: '金額 (元)',
                            },
                            beginAtZero: true,
                        },
                    },
                },
            });
        }
    }

    // 更新結餘折線圖
    function updateBalanceLineChart(data) {
        const ctx = document.getElementById('balanceLineChart').getContext('2d');
        const labels = Object.keys(data);
        const balanceData = labels.map((label) => (data[label]?.income || 0) - (data[label]?.expense || 0));

        if (balanceLineChart) {
            balanceLineChart.data.labels = labels;
            balanceLineChart.data.datasets[0].data = balanceData;
            balanceLineChart.update();
        } else {
            balanceLineChart = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [
                        {
                            label: '結餘',
                            data: balanceData,
                            borderColor: '#FF8C00',
                            backgroundColor: 'rgba(255, 140, 0, 0.2)',
                            tension: 0.4, // 平滑曲線
                            fill: true,
                        },
                    ],
                },
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                    },
                    scales: {
                        x: {
                            title: {
                                display: true,
                                text: '日期',
                            },
                        },
                        y: {
                            title: {
                                display: true,
                                text: '結餘 (元)',
                            },
                            beginAtZero: true,
                        },
                    },
                },
            });
        }
    }

    // 更新卡片的總數據
    function updateSummaryCards(data) {
        const totalIncome = Object.values(data).reduce((sum, entry) => sum + (entry.income || 0), 0);
        const totalExpense = Object.values(data).reduce((sum, entry) => sum + (entry.expense || 0), 0);
        const balance = totalIncome - totalExpense;

        document.getElementById('totalIncome').textContent = totalIncome.toLocaleString();
        document.getElementById('totalExpense').textContent = totalExpense.toLocaleString();
        document.getElementById('balance').textContent = balance.toLocaleString();
    }

    // 按鈕事件綁定
    document.getElementById('last7Days').addEventListener('click', () => fetchChartData('last-7-days'));
    document.getElementById('last30Days').addEventListener('click', () => fetchChartData('last-30-days'));
    document.getElementById('yearly').addEventListener('click', () => fetchChartData('yearly'));

    // 預設載入近 7 天數據
    (async function initialize() {
        await fetchChartData('last-7-days');
    })();
});

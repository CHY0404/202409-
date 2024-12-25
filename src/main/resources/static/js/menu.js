document.addEventListener('DOMContentLoaded', () => {
    const sidebar = document.getElementById('sidebar');
    const mainContent = document.getElementById('main-content');
    const header = document.getElementById('header');
    const memberMenu = document.getElementById('member-menu');
    const toggleButton = document.getElementById('sidebar-toggle');

    toggleButton.addEventListener('click', () => {
        sidebar.classList.toggle('collapsed'); // 切換側邊欄隱藏狀態
        mainContent.classList.toggle('collapsed'); // 更新主內容區域
        header.classList.toggle('collapsed'); // 更新 Header
    });
});

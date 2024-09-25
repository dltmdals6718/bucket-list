$(document).ready(function () {

    $("#prev-page-btn").click(function () {
        let url = new URL(window.location.href);
        let currentPage = parseInt(url.searchParams.get('page')) || 1;
        url.searchParams.set('page', currentPage - 1);
        window.location.href = url.toString();
    });

    $("#next-page-btn").click(function () {
        let url = new URL(window.location.href);
        let currentPage = parseInt(url.searchParams.get('page')) || 1;
        url.searchParams.set('page', currentPage + 1);
        window.location.href = url.toString();
    });
})
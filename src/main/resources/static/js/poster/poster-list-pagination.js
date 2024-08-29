$(document).ready(function () {


    $("#next-page-btn").click(function () {

        let url = new URL(window.location.href);
        let currentPage = parseInt(url.searchParams.get('page')) || 1;
        url.searchParams.set('page', currentPage + 1);
        window.location.href = url.toString();

    });

    $("#prev-page-btn").click(function () {
        let url = new URL(window.location.href);
        let currentPage = parseInt(url.searchParams.get('page')) || 1;
        url.searchParams.set('page', currentPage - 1);
        window.location.href = url.toString();

    });

})
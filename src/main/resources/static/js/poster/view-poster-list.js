$(document).ready(function () {

    // 전체 상태 버튼
    $("#all-status-btn").click(function () {
        let url = new URL(window.location.href);
        url.searchParams.delete("status");
        window.location.href = url.toString();
    });

    // 미완료 상태 버튼
    $("#unachieved-status-btn").click(function () {
        let url = new URL(window.location.href);
        url.searchParams.set("status", "unachieved");
        window.location.href = url.toString();
    });

    // 완료 상태 버튼
    $("#achieved-status-btn").click(function () {
        let url = new URL(window.location.href);
        url.searchParams.set("status", "achieved");
        window.location.href = url.toString();
    });

    // 최신순 정렬
    $("#id-sort-btn").click(function () {
        let url = new URL(window.location.href);
        url.searchParams.set("sort", "id");
        window.location.href = url.toString();
    });

    // 좋아요순 정렬
    $("#like-sort-btn").click(function () {
        let url = new URL(window.location.href);
        url.searchParams.set("sort", "like");
        window.location.href = url.toString();
    });

    $("#comment-sort-btn").click(function () {
        let url = new URL(window.location.href);
        url.searchParams.set("sort", "comment");
        window.location.href = url.toString();
    });



    // 다음 페이지
    $("#next-page-btn").click(function () {

        let url = new URL(window.location.href);
        let currentPage = parseInt(url.searchParams.get('page')) || 1;
        url.searchParams.set('page', currentPage + 1);

        let tags = url.searchParams.get('tags');
        if (tags) {
            url.searchParams.set('tags', tags);
        }

        window.location.href = url.toString();

    });

    // 이전 페이지
    $("#prev-page-btn").click(function () {
        let url = new URL(window.location.href);
        let currentPage = parseInt(url.searchParams.get('page')) || 1;
        url.searchParams.set('page', currentPage - 1);

        let tags = url.searchParams.get('tags');
        if (tags) {
            url.searchParams.set('tags', tags);
        }

        window.location.href = url.toString();

    });

    // 기존 키워드 검색 노출
    const keyword = new URL(window.location.href).searchParams.get('keyword');
    if (keyword) {
        $("#keyword").val(keyword);
    }

    // 기존 검색 태그 노출
    let url = new URL(window.location.href);
    let tagParam = url.searchParams.get('tags');
    if (tagParam) {
        const tags = tagParam.split(',');

        tags.forEach(value => {

            const tagValue = escapeHtml(value);
            const tagHtml = `
                    <li class="d-inline-flex ps-2 me-1 my-1 border rounded bg-light-subtle">
                        <span name="tag">${tagValue}</span>
                        <button name="delete-tag-btn" class="d-inline-flex align-items-center border border-0 bg-transparent">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                 class="bi bi-x" viewBox="0 0 16 16">
                                <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"/>
                            </svg>
                        </button>
                    </li>
                    `;
            $("#tag-list").after(tagHtml);
        });

    }


    // 태그 검색 설정 추가
    $("#tags").on('keypress', function (e) {
        if (e.which == 13) {

            const tagValue = escapeHtml($(this).val().trim());

            if (tagValue) {
                const tagHtml = `
                    <li class="d-inline-flex ps-2 me-1 my-1 border rounded bg-light-subtle">
                        <span name="tag">${tagValue}</span>
                        <button name="delete-tag-btn" class="d-inline-flex align-items-center border border-0 bg-transparent">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                 class="bi bi-x" viewBox="0 0 16 16">
                                <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"/>
                            </svg>
                        </button>
                    </li>
                    `;

                $("#tag-list").after(tagHtml);
                $(this).val('');
            }
        }
    });

    // 태그 삭제 버튼
    $(document).on('click', 'button[name="delete-tag-btn"]', function () {
        $(this).closest('li').remove();
    });

    // 검색 버튼
    $("#search-poster-btn").click(function () {

        // 태그 설정
        const tags = $('[name="tag"]').map(function () {
            return $(this).text();
        }).get().join(',');

        let url = new URL("/posters", window.location.origin);
        if (tags)
            url.searchParams.set('tags', tags);

        // 키워드 설정
        const keyword = $("#keyword").val();
        if (keyword)
            url.searchParams.set('keyword', keyword);

        window.location.href = url.toString();

    });

})
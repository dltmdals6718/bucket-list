let page = new URL(window.location.href).searchParams.get('page');
if (!page)
    page = 1;

$(document).ready(function () {

    // 게시글 목록 호출
    loadPosterOverview();

    // 다음 페이지
    $("#next-page-btn").click(function () {
        page++;
        loadPosterOverview();
    });

    // 이전 페이지
    $("#prev-page-btn").click(function () {
        page--;
        loadPosterOverview();
    });


});

function loadPosterOverview() {

    $("#all-posters-overview").empty();

    let url = new URL(window.location.href);

    let size = url.searchParams.get('size');
    let tags = url.searchParams.get('tags');
    let keyword = url.searchParams.get('keyword');
    let status = url.searchParams.get('status');
    const curPosterId = $("#all-posters-overview").attr("data-poster-id");

    // 게시글 목록 설정
    let posterRequestUrl = '/api/posters';
    let posterRequestParam = '';
    let queryParam = [];
    if (page) queryParam.push(`page=${page}`);
    if (size) queryParam.push(`size=${size}`);
    if (tags) queryParam.push(`tags=${tags}`);
    if (keyword) queryParam.push(`keyword=${keyword}`);
    if (status) queryParam.push(`status=${status}`);

    if (queryParam.length > 0) {
        posterRequestUrl += '?' + queryParam.join('&');
        posterRequestParam += '?' + queryParam.join('&');
    }

    $.ajax(posterRequestUrl, {
        type: "GET",
        dataType: "json",
        success: function (data) {

            const posters = data.content;
            const pageInfo = data.page;

            for (const poster of posters) {

                const posterId = poster.posterId;
                let backGroundColorClass = "";
                let boldClass = "";

                if (curPosterId == posterId) {
                    backGroundColorClass = "bg-body-secondary";
                    boldClass = "fw-semibold";
                }

                $("#all-posters-overview").append(`
                    <li class="list-group-item px-0 ${backGroundColorClass}">
                        <div class="d-flex">
                            <div class="text-truncate">
                                <a class="link-dark link-underline-opacity-0 ${boldClass}" href="/posters/${poster.posterId}${posterRequestParam}">${poster.title}</a> 
                            </div>
                            <div class="d-flex ms-auto align-items-center" style="gap: 5px;">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                    class="bi bi-hand-thumbs-up" viewBox="0 0 16 16">
                                    <path d="M8.864.046C7.908-.193 7.02.53 6.956 1.466c-.072 1.051-.23 2.016-.428 2.59-.125.36-.479 1.013-1.04 1.639-.557.623-1.282 1.178-2.131 1.41C2.685 7.288 2 7.87 2 8.72v4.001c0 .845.682 1.464 1.448 1.545 1.07.114 1.564.415 2.068.723l.048.03c.272.165.578.348.97.484.397.136.861.217 1.466.217h3.5c.937 0 1.599-.477 1.934-1.064a1.86 1.86 0 0 0 .254-.912c0-.152-.023-.312-.077-.464.201-.263.38-.578.488-.901.11-.33.172-.762.004-1.149.069-.13.12-.269.159-.403.077-.27.113-.568.113-.857 0-.288-.036-.585-.113-.856a2 2 0 0 0-.138-.362 1.9 1.9 0 0 0 .234-1.734c-.206-.592-.682-1.1-1.2-1.272-.847-.282-1.803-.276-2.516-.211a10 10 0 0 0-.443.05 9.4 9.4 0 0 0-.062-4.509A1.38 1.38 0 0 0 9.125.111zM11.5 14.721H8c-.51 0-.863-.069-1.14-.164-.281-.097-.506-.228-.776-.393l-.04-.024c-.555-.339-1.198-.731-2.49-.868-.333-.036-.554-.29-.554-.55V8.72c0-.254.226-.543.62-.65 1.095-.3 1.977-.996 2.614-1.708.635-.71 1.064-1.475 1.238-1.978.243-.7.407-1.768.482-2.85.025-.362.36-.594.667-.518l.262.066c.16.04.258.143.288.255a8.34 8.34 0 0 1-.145 4.725.5.5 0 0 0 .595.644l.003-.001.014-.003.058-.014a9 9 0 0 1 1.036-.157c.663-.06 1.457-.054 2.11.164.175.058.45.3.57.65.107.308.087.67-.266 1.022l-.353.353.353.354c.043.043.105.141.154.315.048.167.075.37.075.581 0 .212-.027.414-.075.582-.05.174-.111.272-.154.315l-.353.353.353.354c.047.047.109.177.005.488a2.2 2.2 0 0 1-.505.805l-.353.353.353.354c.006.005.041.05.041.17a.9.9 0 0 1-.121.416c-.165.288-.503.56-1.066.56z"/>
                                </svg>
                                <span style="font-size: 10pt;">0</span>
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                    class="bi bi-chat-right-dots ms-1" viewBox="0 0 16 16">
                                    <path d="M2 1a1 1 0 0 0-1 1v8a1 1 0 0 0 1 1h9.586a2 2 0 0 1 1.414.586l2 2V2a1 1 0 0 0-1-1zm12-1a2 2 0 0 1 2 2v12.793a.5.5 0 0 1-.854.353l-2.853-2.853a1 1 0 0 0-.707-.293H2a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2z"/>
                                    <path d="M5 6a1 1 0 1 1-2 0 1 1 0 0 1 2 0m4 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m4 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/>
                                </svg>
                                <span style="font-size: 10pt;">0</span>
                            </div>                    
                        </div>                    
                `)

            }

            // 이전 페이지 버튼 상태 설정
            if (pageInfo.number <= 0)
                $("#prev-page-btn").attr("disabled", true);
            else
                $("#prev-page-btn").removeAttr("disabled");

            // 다음 페이지 버튼 설정
            if (pageInfo.number >= pageInfo.totalPages - 1) {
                $("#next-page-btn").attr("disabled", true);
            } else {
                $("#next-page-btn").removeAttr("disabled");
            }


        },
        error: function (xhr) {
            alert("실패");
        }
    })
}
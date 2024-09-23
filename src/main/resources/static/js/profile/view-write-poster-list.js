let writePosterPage = 1;
$(document).ready(function () {

    loadWritePosterOverview(writePosterPage);

    $("#write-poster-overview-prev-page-btn").click(function () {
        writePosterPage--;
        loadWritePosterOverview(writePosterPage);
    })

    $("#write-poster-overview-next-page-btn").click(function () {
        writePosterPage++;
        loadWritePosterOverview(writePosterPage);
    })

});

function loadWritePosterOverview(page) {

    $.ajax(`/api/write-posters?page=${page}`, {
        type: "GET",
        dataType: "json",
        success: function (data) {

            $("#write-poster-overview").empty();

            const posters = data.content;
            const pageInfo = data.page;

            writePosterOverviewPagination(pageInfo.number, pageInfo.totalPages);



            // 이전 페이지 버튼 상태 설정
            if (page <= 1) {
                $("#write-poster-overview-prev-page-btn").attr("disabled", true);
            } else {
                $("#write-poster-overview-prev-page-btn").removeAttr("disabled");
            }

            // 다음 페이지 버튼 설정
            if (page >= pageInfo.totalPages) {
                $("#write-poster-overview-next-page-btn").attr("disabled", true);
            } else {
                $("#write-poster-overview-next-page-btn").removeAttr("disabled");
            }


            for (const poster of posters) {

                let posterItem = `
                <li class="list-group-item">
                            <div>
                                <div class="d-flex">
                                    <div class="me-1">
                                        <img class="rounded-circle" src="${poster.profileImg}" width="20" height="20">
                                    </div>
                                    <div class="text-truncate align-self-center" style="font-size: 10pt;">
                                        <a class="link-dark link-underline-opacity-0 m-0" href="#">${poster.nickname}</a>
                                    </div>
                                </div>
                                <div class="d-flex">
                                    <div class="text-truncate">
                                        <a class="link-dark link-underline-opacity-0 fw-semibold"
                                            href="/posters/${poster.posterId}">${poster.title}</a>
                                    </div>
                `;

                if (poster.isAchieve) {
                    posterItem += `
                <div class="d-flex align-items-center ms-1">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16"
                        height="16" fill="#55abed" class="bi bi-patch-check-fill" viewBox="0 0 16 16">
                        <path d="M10.067.87a2.89 2.89 0 0 0-4.134 0l-.622.638-.89-.011a2.89 2.89 0 0 0-2.924 2.924l.01.89-.636.622a2.89 2.89 0 0 0 0 4.134l.637.622-.011.89a2.89 2.89 0 0 0 2.924 2.924l.89-.01.622.636a2.89 2.89 0 0 0 4.134 0l.622-.637.89.011a2.89 2.89 0 0 0 2.924-2.924l-.01-.89.636-.622a2.89 2.89 0 0 0 0-4.134l-.637-.622.011-.89a2.89 2.89 0 0 0-2.924-2.924l-.89.01zm.287 5.984-3 3a.5.5 0 0 1-.708 0l-1.5-1.5a.5.5 0 1 1 .708-.708L7 8.793l2.646-2.647a.5.5 0 0 1 .708.708"/>
                    </svg>
                </div>
                `
                }

                posterItem += `
                </div>
                    <div class="text-truncate">
                        <a class="link-dark link-underline-opacity-0" href="/posters/${poster.posterId}" style="font-size: 10pt">${poster.content}</a>
                    </div>
                `

                if (poster.tags.length > 0) {

                    posterItem += `<ul class="px-0">`;

                    poster.tags.forEach(tag => {
                        posterItem += `<li class="d-inline-flex px-2 me-1 my-1 border rounded bg-light-subtle">`;
                        posterItem += `<span class="text-secondary"style="font-size: 10pt">${tag}</span>`;
                        posterItem += `</li>`;
                    });
                }

                posterItem += `
                    <div class="d-flex">
                        <div>
                            <span class="m-0 text-secondary" style="font-size: 10pt">${poster.createdDate}</span>
                        </div>
                        <div class="ms-auto">
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
                        </div>
                    </li>
                `

                $("#write-poster-overview").append(posterItem);
            }

        },
        error: function (xhr) {
            alert("실패");
        }
    })
}

function writePosterOverviewPagination(number, totalPage) {

    const paginationContainer = $("#write-poster-overview-pagination");
    paginationContainer.empty();

    if (totalPage == 0)
        return;

    const visiblePageCount = 10;
    const curPage = number + 1;
    const curPageBlock = Math.ceil(curPage / visiblePageCount);
    const startPage = (curPageBlock - 1) * visiblePageCount + 1;
    const endPage = Math.min(curPageBlock * visiblePageCount, totalPage);

    for (let i = startPage; i<=endPage; i++) {

        let curPageClass = "";

        if (i == number + 1)
            curPageClass = "bg-primary-subtle";

        const pageLink = $(`<a class="link-dark link-underline-opacity-0 border px-2 py-1 mx-1 ${curPageClass}">${i}</a>`);
        pageLink.click(function () {
            loadWritePosterOverview(i);
            writePosterPage = i;
        });

        paginationContainer.append(pageLink);
    }



}
$(document).ready(function () {

    let allPostersOverviewCurPage = 1;

    loadAllPosters(allPostersOverviewCurPage);

    $("#all-posters-prev-page-btn").click(function () {
        allPostersOverviewCurPage--;
        loadAllPosters(allPostersOverviewCurPage);
    })

    $("#all-posters-next-page-btn").click(function () {
        allPostersOverviewCurPage++;
        loadAllPosters(allPostersOverviewCurPage);
    })

});

function clearAllPostersOverview() {
    $("#all-posters-overview").empty();
}

function loadAllPosters(page) {

    // 전체 게시글
    $.ajax(`/api/posters?page=${page}`, {
        type: "GET",
        dataType: "json",
        success: function (data) {

            clearAllPostersOverview();

            const posters = data.content;
            const pageInfo = data.page;

            // 이전 페이지 버튼 상태 설정
            if (page <= 1) {
                $("#all-posters-prev-page-btn").attr("disabled", true);
            } else {
                $("#all-posters-prev-page-btn").removeAttr("disabled");
            }

            // 다음 페이지 버튼 설정
            if (page >= pageInfo.totalPages) {
                $("#all-posters-next-page-btn").attr("disabled", true);
            } else {
                $("#all-posters-next-page-btn").removeAttr("disabled");
            }


            for (const poster of posters) {

                $("#all-posters-overview").append(`
                    <li>
                        <div>
                            <img src="${poster.profileImg}" width="20" height="20">
                            <a class="link-dark link-underline-opacity-0 m-0" href="#">${poster.nickname}</a>
                            <span class="m-0 text-secondary" style="font-size: 10pt">${poster.createdDate}</span>
                            <div>
                                <a class="link-dark link-underline-opacity-0" href="/posters/${poster.posterId}">${poster.title}</a>
                            </div>
                        </div>
                    </li>
                `)

            }

        },
        error: function (xhr) {
            alert("실패");
        }
    })

}



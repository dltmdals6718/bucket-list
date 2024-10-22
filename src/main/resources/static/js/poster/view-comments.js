let commentPage = 1;

$(document).ready(function () {


    const posterId = $("#comments-overview").attr("data-poster-id");

    loadComments(posterId, commentPage);

    $("#more-comments-btn").click(function () {
        commentPage++;
        loadComments(posterId, commentPage);
    });

});

function loadComments(posterId, commentPage) {

    $.ajax(`/api/posters/${posterId}/comments?page=${commentPage}`, {
        type: "GET",
        dataType : "json",
        success: function (data) {

            const comments = data.content;
            const pageInfo = data.page;

            if (pageInfo.number >= pageInfo.totalPages - 1) {
                $("#more-comments-btn").remove();
            }

            for (const comment of comments) {
                
                const name = $("<a></a>")
                    .text(unescapeHtml(comment.nickname));

                const commentElement = $("<li></li>")
                    .text(unescapeHtml(comment.content))
                    .append(name);

                $("#comments-overview").append(commentElement);

            }




        },
        error: function (xhr) {
            alert("댓글 로드 실패");
        }
    });

}
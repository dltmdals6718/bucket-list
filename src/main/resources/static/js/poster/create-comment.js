$(document).ready(function () {

    // 댓글 작성 버튼
    $("#comment-write-btn").click(function () {

        const csrfToken = $('meta[name="_csrf"]').attr("content");
        const csrfHeader = $('meta[name="_csrf_header"]').attr("content");
        const posterId = $("#comment").attr("data-poster-id");
        const content = $("#comment").val();

        $.ajax("/api/comments/write/" + posterId, {
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({
                content: content
            }),
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data) {
                $("#comments-overview").empty();
                loadComments(posterId, commentPage);
                $("#comment").val('');
            },
            error: function (xhr) {
                const responseJson = JSON.parse(xhr.responseText);

                for (const key in responseJson) {
                    alert(responseJson[key]);
                    break;
                }

            }
        })


    });
});
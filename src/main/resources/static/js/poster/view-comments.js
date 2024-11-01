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
                    .text(unescapeHtml(comment.nickname))
                    .attr("href", "/members/" + comment.memberId + "/posters");

                const content = $("<div class='comment-content'></div>")
                    .html(unescapeHtml(comment.content));

                const commentElement = $("<li></li>")
                    .append(content)
                    .append(name);

                const deleteBtn = $("<a></a>")
                    .text("삭제")
                    .attr("href", "#")
                    .on("click", function (e) {
                        e.preventDefault();

                        if (confirm("정말 삭제하시겠습니까?") == false)
                            return;

                        const csrfToken = $('meta[name="_csrf"]').attr("content");
                        const csrfHeader = $('meta[name="_csrf_header"]').attr("content");



                        $.ajax("/api/posters/" + comment.posterId + "/comments/" + comment.commentId, {
                            type: "DELETE",
                            beforeSend: function (xhr) {
                                xhr.setRequestHeader(csrfHeader, csrfToken);
                            },
                            success: function (data) {
                                alert("댓글 삭제 성공.")
                                window.location = "/posters/" + comment.posterId;
                            },
                            error:  function (xhr, status, error) {
                                alert("댓글 삭제 요청 실패.");
                            }
                        })
                    });

                const updateBtn = $("<a></a>")
                    .text("수정")
                    .attr("href", "#")
                    .on("click", function (e) {
                        e.preventDefault();

                        const contentDiv = $(this).siblings("div[class='comment-content']");
                        contentDiv.remove();

                        const commentForm = $('<textarea id="comment-update-form"></textarea>')
                            .text(contentDiv.text());

                        const updateConfirm = $('<button></button>')
                            .addClass('btn btn-light')
                            .text('댓글 수정');

                        $(this).parent()
                            .prepend(updateConfirm)
                            .prepend(commentForm);

                        updateConfirm
                            .click(function () {
                                if (confirm("정말 수정하시겠습니까?") == false)
                                    return;

                                const csrfToken = $('meta[name="_csrf"]').attr("content");
                                const csrfHeader = $('meta[name="_csrf_header"]').attr("content");
                                const commentUpdateContent = $(this).siblings($("#comment-update-form")).val();

                                $.ajax("/api/posters/" + comment.posterId + "/comments/" + comment.commentId, {
                                    type: "PUT",
                                    contentType: "application/json",
                                    data: JSON.stringify({
                                        content: commentUpdateContent
                                    }),
                                    beforeSend: function (xhr) {
                                        xhr.setRequestHeader(csrfHeader, csrfToken);
                                    },
                                    success: function (data) {
                                        alert("댓글 수정 완료.")
                                        window.location = "/posters/" + comment.posterId;
                                    },
                                    error:  function (xhr, status, error) {
                                        const responseJson = JSON.parse(xhr.responseText);

                                        for (const key in responseJson) {
                                            alert(responseJson[key]);
                                            break;
                                        }
                                    }
                                })
                            });
                    });


                if (comment.isOwner) {
                    commentElement.append(updateBtn);
                    commentElement.append(deleteBtn);
                }

                $("#comments-overview").append(commentElement);

            }

        },
        error: function (xhr) {
            alert("댓글 로드 실패");
        }
    });

}
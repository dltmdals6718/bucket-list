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
                    .attr("href", "/members/" + comment.memberId + "/posters")
                    .attr("class", "link-dark link-underline-opacity-0");

                const addLike = $("<button></button>")
                    .addClass("ms-auto btn")
                    .append(`<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" className="bi bi-hand-thumbs-up" viewBox="0 0 16 16">
                        <path d="M8.864.046C7.908-.193 7.02.53 6.956 1.466c-.072 1.051-.23 2.016-.428 2.59-.125.36-.479 1.013-1.04 1.639-.557.623-1.282 1.178-2.131 1.41C2.685 7.288 2 7.87 2 8.72v4.001c0 .845.682 1.464 1.448 1.545 1.07.114 1.564.415 2.068.723l.048.03c.272.165.578.348.97.484.397.136.861.217 1.466.217h3.5c.937 0 1.599-.477 1.934-1.064a1.86 1.86 0 0 0 .254-.912c0-.152-.023-.312-.077-.464.201-.263.38-.578.488-.901.11-.33.172-.762.004-1.149.069-.13.12-.269.159-.403.077-.27.113-.568.113-.857 0-.288-.036-.585-.113-.856a2 2 0 0 0-.138-.362 1.9 1.9 0 0 0 .234-1.734c-.206-.592-.682-1.1-1.2-1.272-.847-.282-1.803-.276-2.516-.211a10 10 0 0 0-.443.05 9.4 9.4 0 0 0-.062-4.509A1.38 1.38 0 0 0 9.125.111zM11.5 14.721H8c-.51 0-.863-.069-1.14-.164-.281-.097-.506-.228-.776-.393l-.04-.024c-.555-.339-1.198-.731-2.49-.868-.333-.036-.554-.29-.554-.55V8.72c0-.254.226-.543.62-.65 1.095-.3 1.977-.996 2.614-1.708.635-.71 1.064-1.475 1.238-1.978.243-.7.407-1.768.482-2.85.025-.362.36-.594.667-.518l.262.066c.16.04.258.143.288.255a8.34 8.34 0 0 1-.145 4.725.5.5 0 0 0 .595.644l.003-.001.014-.003.058-.014a9 9 0 0 1 1.036-.157c.663-.06 1.457-.054 2.11.164.175.058.45.3.57.65.107.308.087.67-.266 1.022l-.353.353.353.354c.043.043.105.141.154.315.048.167.075.37.075.581 0 .212-.027.414-.075.582-.05.174-.111.272-.154.315l-.353.353.353.354c.047.047.109.177.005.488a2.2 2.2 0 0 1-.505.805l-.353.353.353.354c.006.005.041.05.041.17a.9.9 0 0 1-.121.416c-.165.288-.503.56-1.066.56z"/>
                        </svg>`)
                    .on("click", function (e) {

                        const csrfToken = $('meta[name="_csrf"]').attr("content");
                        const csrfHeader = $('meta[name="_csrf_header"]').attr("content");

                        $.ajax("/api/comments-like/" + comment.commentId, {
                            type: "POST",
                            dataType: "application/json",
                            beforeSend: function (xhr) {
                                xhr.setRequestHeader(csrfHeader, csrfToken);
                            },
                            success: function (data) {
                                alert("댓글 좋이요 완료.");
                            },
                            error:  function (xhr, status, error) {
                                const responseJson = JSON.parse(xhr.responseText);

                                for (const key in responseJson) {
                                    alert(responseJson[key]);
                                    break;
                                }
                            }
                        });

                    });

                const nameDiv = $("<div></div>")
                    .append(name);

                const date = $("<p></p>")
                    .attr("class", "text-secondary")
                    .text(comment.createdDate);

                const profileImg = $("<img>")
                    .attr("src", comment.profileImg)
                    .attr("class", "rounded-circle me-2")
                    .attr("width", "48px")
                    .attr("height", "48px");

                const subProfileDetails = $("<div></div>")
                    .attr("class", "d-flex")
                    .append(date);

                const profileImgLink = $("<a></a>")
                    .attr("href", "/members/" + comment.memberId + "/posters")
                    .css({
                        width: "48px",
                        height: "48px",
                        marginRight: "8px"
                    })
                    .append(profileImg);

                const profileDetails = $("<div></div>")
                    .append(nameDiv)
                    .append(subProfileDetails);

                const profile = $("<div></div>")
                    .attr("class", "d-flex")
                    .append(profileImgLink)
                    .append(profileDetails)
                    .append(addLike);

                const content = $("<div class='comment-content'></div>")
                    .html(unescapeHtml(comment.content));

                const commentElement = $("<li></li>")
                    .attr("class", "list-group-item px-0")
                    .append(profile)
                    .append(content);

                const deleteBtn = $("<a></a>")
                    .text("삭제")
                    .attr("href", "#")
                    .attr("class", "ms-1 link-primary link-underline-opacity-0")
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

                let editFormFlag = false;

                const updateBtn = $("<a></a>")
                    .text("수정")
                    .attr("href", "#")
                    .attr("class", "ms-2 link-primary link-underline-opacity-0")
                    .on("click", function (e) {
                        e.preventDefault();

                        if (editFormFlag)
                            return;

                        editFormFlag = true;

                        const commentForm = $('<textarea id="comment-update-form"></textarea>')
                            .addClass("w-100")
                            .text(unescapeHtml(content.html().replaceAll("<br>", "\n")));

                        content.remove();

                        const updateConfirm = $('<button></button>')
                            .addClass('btn btn-light flex-grow-1')
                            .text('댓글 수정');

                        const updateCancel = $('<button></button>')
                            .addClass('btn btn-light')
                            .text('취소')
                            .click(function () {
                                commentForm.remove();
                                updateConfirm.remove();
                                updateCancel.remove();
                                commentElement.append(content);
                                editFormFlag = false;
                            });

                        const updateDiv = $("<div></div>")
                            .attr("class", "d-flex")
                            .append(updateConfirm)
                            .append(updateCancel);

                        commentElement
                            .append(commentForm)
                            .append(updateDiv);

                        updateConfirm
                            .click(function () {
                                if (confirm("정말 수정하시겠습니까?") == false)
                                    return;

                                const csrfToken = $('meta[name="_csrf"]').attr("content");
                                const csrfHeader = $('meta[name="_csrf_header"]').attr("content");
                                const commentUpdateContent = commentForm.val();

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
                    subProfileDetails.append(updateBtn);
                    subProfileDetails.append(deleteBtn);
                }

                $("#comments-overview").append(commentElement);

            }

        },
        error: function (xhr) {
            alert("댓글 로드 실패");
        }
    });

}
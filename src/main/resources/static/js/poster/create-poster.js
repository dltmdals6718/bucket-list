$(document).ready(function () {

    const csrfToken = $('meta[name="_csrf"]').attr("content");
    const csrfHeader = $('meta[name="_csrf_header"]').attr("content");

    $("#createPosterBtn").click(function () {
        const isPrivate = $("#isPrivate").is(':checked');
        const title = $("#title").val();
        const content = editor.getData();
        const tags = $('[name = "tag"]').map(function () {
            return $(this).text();
        }).get();

        const poster = {
            title: title,
            content: content,
            isPrivate: isPrivate,
            tags: tags
        };

        const formData = new FormData();
        formData.append("poster", new Blob([JSON.stringify(poster)], {type: "application/json"}));

        $.ajax("/api/posters/write", {
            type: "POST",
            enctype: "multipart/form-data",
            contentType: false,
            processData: false,
            data: formData,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data) {
                window.location = "/posters/" + data.posterId;
            },
            error: function (xhr, status, error) {

                $(".error-message").remove();

                const responseJson = JSON.parse(xhr.responseText);
                for (const key in responseJson) {

                    const field = $("#" + key);
                    const errorMsg = $("<div></div>", {
                        class: "error-message",
                        css: {color: "orange"}
                    });
                    const message =
                        "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='orange' style='margin: 5px' class='bi bi-exclamation-circle' viewBox='0 0 16 16'>\n" +
                        "<path d='M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16'/>\n" +
                        "<path d='M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z'/>\n" +
                        "</svg>" + responseJson[key];

                    errorMsg.html(message);
                    field.after(errorMsg);

                }
            }
        });
    });

    $("#tags").on('keypress', function (e) {
        if (e.which == 13) {

            const tagValue = $(this).val().trim();

            if (tagValue) {
                const tagHtml = `
                    <li class="d-inline-flex ps-2 me-1 my-1 border rounded bg-light-subtle">
                        <span name="tag">${escapeHtml(tagValue)}</span>
                        <button name="delete-tag-btn" class="d-inline-flex align-items-center border border-0 bg-transparent">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                                 class="bi bi-x" viewBox="0 0 16 16">
                                <path d="M4.646 4.646a.5.5 0 0 1 .708 0L8 7.293l2.646-2.647a.5.5 0 0 1 .708.708L8.707 8l2.647 2.646a.5.5 0 0 1-.708.708L8 8.707l-2.646 2.647a.5.5 0 0 1-.708-.708L7.293 8 4.646 5.354a.5.5 0 0 1 0-.708"/>
                            </svg>
                        </button>
                    </li>
                    `;

                $(this).before(tagHtml);
                $(this).val('');
            }
        }
    });

});

// 태그 삭제 버튼
$(document).on('click', 'button[name="delete-tag-btn"]', function () {
    $(this).closest('li').remove();
});


function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
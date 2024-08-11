$(document).ready(function () {

    const csrfToken = $('meta[name="_csrf"]').attr("content");
    const csrfHeader = $('meta[name="_csrf_header"]').attr("content");

    $("#createPosterBtn").click(function () {
        const isPrivate = $("#isPrivate").is(':checked');
        const title = $("#title").val();
        const content = editor.getData();
        const poster = {
            title: title,
            content: content,
            isPrivate: isPrivate
        };

        const formData = new FormData();
        formData.append("poster", new Blob([JSON.stringify(poster)], {type: "application/json"}));

        $.ajax("/posters/write", {
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
});
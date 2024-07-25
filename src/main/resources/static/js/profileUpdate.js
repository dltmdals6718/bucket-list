document.addEventListener("DOMContentLoaded", function () {

    // 프로필 업데이터 버튼
    document.getElementById("profileUpdateBtn").addEventListener("click", function (evt) {

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");
        
        const nickname = document.getElementById("nickname").value;
        const profileImg = $("#profileImg")[0].files[0];

        const profile = {
            nickname: nickname,
        };

        const formData = new FormData();
        formData.append("profile", new Blob([JSON.stringify(profile)], {type: "application/json"}));
        formData.append("profileImg", profileImg);

        $.ajax("/members/profile", {
            type: "PUT",
            enctype: "multipart/form-data",
            contentType: false,
            processData: false,
            data: formData,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (data) {
                alert("프로필 정보 변경 완료");
                location.href = "/members/profile";
            },
            error: function (xhr, status, error) {

                const errorMessages = document.getElementsByClassName("error-message");

                while (errorMessages.length > 0) {
                    errorMessages[0].remove();
                }

                const responseJson = JSON.parse(xhr.responseText);
                for (const key in responseJson) {
                    const target = document.getElementById(key);
                    const errorMsg = document.createElement('div');
                    errorMsg.className = "error-message";
                    errorMsg.style.color = "orange";
                    errorMsg.innerHTML =
                        "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='orange' style='margin: 5px' class='bi bi-exclamation-circle' viewBox='0 0 16 16'>\n" +
                        "<path d='M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16'/>\n" +
                        "<path d='M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z'/>\n" +
                        "</svg>" + responseJson[key];
                    target.insertAdjacentElement('afterend', errorMsg);
                }
            }
        })

    })

    // 프로필 이미지 미리보기
    $("#profileImg").change(function () {
        let select_img = this.files[0];
        if (select_img) {
            let reader = new FileReader();
            reader.readAsDataURL(select_img);
            reader.onload = function () {
                $("#preview").attr("src", reader.result);
            }
        }
    });

    // 프로필 이미지 클릭시 파일 업로드
    $("#profileImgBtn").click(function () {
        $("#profileImg").click();
    });

    $("#deleteProfileImg").click(function (event) {
        event.stopPropagation();

        const csrfToken = $('meta[name="_csrf"]').attr("content");
        const csrfHeader = $('meta[name="_csrf_header"]').attr("content");

        if (confirm("정말 기본 프로필 이미지로 변경하시겠습니까?") == true) {

            $.ajax("/members/profileImg", {
                type: "DELETE",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (data) {
                    $("#preview").attr("src", data);
                    $("#profileImgHeaderPreview").attr("src", data);
                },
                error: function (xhr, status, error) {
                    alert("프로필 이미지 삭제 실패.");
                }
            });
        } else {
            return;
        }

    });

    // 비밀번호 변경 버튼
    $("#pwdUpdateBtn").click(function () {

        const csrfToken = $('meta[name="_csrf"]').attr("content");
        const csrfHeader = $('meta[name="_csrf_header"]').attr("content");

        const password = $('#password').val();
        const newPassword = $('#newPassword').val();
        const confirmNewPassword = $('#confirmNewPassword').val();

        const data = {
            password: password,
            newPassword: newPassword,
            confirmNewPassword: confirmNewPassword
        };

        $.ajax("/members/password", {
            type: "PUT",
            data: data,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken)
            },
            success: function (data) {
                alert("비밀번호가 변경되었습니다.");
                window.location = "/members/profile";
            },
            error: function (xhr, status, error) {

                const errorMessages = document.getElementsByClassName("error-message");

                while (errorMessages.length > 0) {
                    errorMessages[0].remove();
                }

                const responseJson = JSON.parse(xhr.responseText);
                for (const key in responseJson) {
                    const target = document.getElementById(key);
                    const errorMsg = document.createElement('div');
                    errorMsg.className = "error-message";
                    errorMsg.style.color = "orange";
                    errorMsg.innerHTML =
                        "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' fill='orange' style='margin: 5px' class='bi bi-exclamation-circle' viewBox='0 0 16 16'>\n" +
                        "<path d='M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16'/>\n" +
                        "<path d='M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z'/>\n" +
                        "</svg>" + responseJson[key];
                    target.insertAdjacentElement('afterend', errorMsg);
                }

            }
        })
    });

})
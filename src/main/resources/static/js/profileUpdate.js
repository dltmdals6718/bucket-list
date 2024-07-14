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
                    errorMsg.textContent = responseJson[key];
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

        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        if (confirm("정말 기본 프로필 이미지로 변경하시겠습니까?") == true) {

            $.ajax("/members/profileImg", {
                type: "DELETE",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function (data) {
                    $("#preview").attr("src", data);
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
                    errorMsg.textContent = responseJson[key];
                    target.insertAdjacentElement('afterend', errorMsg);
                }

            }
        })
    });

})
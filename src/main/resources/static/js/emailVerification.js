document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("emailVerificationBtn").addEventListener("click", function (evt) {

        const email = document.getElementById("email").value;
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

        if (!emailRegex.test(email)) {
            alert("올바른 이메일 형식으로 작성해주세요.")
        } else {
            $.ajax("/mails", {
                type: "POST",
                data: JSON.stringify({"email": email}),
                beforeSend: function (xhr) {
                  xhr.setRequestHeader("Content-Type", "application/json");
                  xhr.setRequestHeader(csrfHeader, csrfToken);
                },
                success: function () {
                    alert(email + "로 전송 완료.")
                },
                error: function () {
                    alert(email + "로 전송 실패.")
                }
            })
        }
    })
});
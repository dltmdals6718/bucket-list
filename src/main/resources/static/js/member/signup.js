$(document).ready(function () {

    $("#kakaoLoginBtn").click(function () {
        window.location = "/oauth2/authorization/kakao";
    });

    $("#googleLoginBtn").click(function () {
        window.location = "/oauth2/authorization/google";
    });

    $("#naverLoginBtn").click(function () {
        window.location = "/oauth2/authorization/naver";
    });

});
document.addEventListener("DOMContentLoaded", function () {

    function moveProfileImg() {

        const profileImgHeader = document.getElementById('profileImgHeader');
        const mobileMenuButton = document.getElementById('mobileMenuButton');
        const expand = document.getElementById('expand');

        if (profileImgHeader) {
            if (window.innerWidth < 992) {
                if (mobileMenuButton.previousSibling !== profileImgHeader) {
                    mobileMenuButton.parentNode.insertBefore(profileImgHeader, mobileMenuButton);
                }
            } else {
                if (expand.nextSibling !== profileImgHeader) {
                    expand.parentNode.insertBefore(profileImgHeader, expand.nextSibling);
                }
            }
        }
    }

    window.addEventListener('resize', moveProfileImg);
    moveProfileImg();
});

function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}
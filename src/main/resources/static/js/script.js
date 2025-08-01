document.querySelectorAll('.clickable-text').forEach(element => {
    element.addEventListener('click', function() {
        const sentence = this.textContent.trim();
        const translation = translationMap[sentence];
        if (translation) {
            alert(translation);
        }
    });
});
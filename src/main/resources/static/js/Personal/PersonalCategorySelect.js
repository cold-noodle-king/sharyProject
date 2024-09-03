$(document).ready(function() {
    const maxSelection = 3;
    let selectedCategories = new Set();

    $('.category-button').on('click', function() {
        const category = $(this).data('category');

        if (selectedCategories.has(category)) {
            selectedCategories.delete(category);
            $(this).removeClass('btn-warning').addClass('btn-secondary');
        } else {
            if (selectedCategories.size < maxSelection) {
                selectedCategories.add(category);
                $(this).removeClass('btn-secondary').addClass('btn-warning');
            } else {
                alert('최대 3개의 카테고리만 선택할 수 있습니다.');
            }
        }
    });
});
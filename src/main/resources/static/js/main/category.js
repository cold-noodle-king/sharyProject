document.addEventListener("DOMContentLoaded", function () {
    let selectedCategories = []; // 선택된 카테고리를 저장할 배열

    // 모든 카테고리 버튼에 클릭 이벤트 리스너 추가
    const buttons = document.querySelectorAll('.cat-btn');
    buttons.forEach(button => {
        button.addEventListener('click', function () {
            const categoryNum = this.getAttribute('data-cat-num');

            // 이미 선택된 카테고리인 경우 제거
            if (selectedCategories.includes(categoryNum)) {
                selectedCategories = selectedCategories.filter(cat => cat !== categoryNum);
                this.classList.remove('btn-primary');
                this.classList.add('btn-secondary');
            } else {
                // 3개 이하일 때만 추가
                if (selectedCategories.length < 3) {
                    selectedCategories.push(categoryNum);
                    this.classList.remove('btn-secondary');
                    this.classList.add('btn-primary');
                } else {
                    alert("최대 3개의 카테고리만 선택할 수 있습니다.");
                }
            }
        });
    });

    // 저장 버튼 클릭 시 폼 전송
    document.getElementById('save').addEventListener('click', function () {
        if (selectedCategories.length > 0) {
            // 선택된 카테고리 번호를 hidden input에 추가
            document.getElementById('selectedCategory').value = selectedCategories.join(','); // 콤마로 구분하여 전달
            console.log('선택된 카테고리:', document.getElementById('selectedCategory').value);
            document.getElementById('categoryForm').submit(); // 폼 제출
        } else {
            alert("카테고리를 선택해주세요!"); // 카테고리 미선택 시 경고
        }
    });
});
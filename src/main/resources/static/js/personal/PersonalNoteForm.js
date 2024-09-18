window.onload = function() {
    // Kakao Maps API 로드 여부 확인
    if (typeof kakao !== 'undefined') {
        var mapContainer = document.getElementById('map'),
            mapOption = {
                center: new kakao.maps.LatLng(33.450701, 126.570667), // 초기 지도 중심 좌표
                level: 3 // 초기 지도 확대 레벨
            };

        var map = null; // 지도 객체를 null로 초기화
        var marker = null; // 마커 객체도 null로 초기화
        var selectedAddress = ''; // 선택된 주소를 저장할 변수
        var lastCoords = null; // 마지막 검색된 좌표를 저장하는 변수
        var ps = new kakao.maps.services.Places(); // 장소 검색을 위한 Places 서비스 객체 생성

        // 모달이 열릴 때 지도 초기화 또는 재로드
        document.getElementById('mapModal').addEventListener('shown.bs.modal', function () {
            if (!map) { // 지도 객체가 없을 때만 초기화
                map = new kakao.maps.Map(mapContainer, mapOption);
                marker = new kakao.maps.Marker({
                    position: map.getCenter() // 초기 마커 위치
                });
                marker.setMap(map);

                // 지도 클릭 시 주소 및 장소 정보 검색
                kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
                    var latlng = mouseEvent.latLng; // 클릭한 좌표

                    var geocoder = new kakao.maps.services.Geocoder();

                    // 주소 정보 검색
                    geocoder.coord2Address(latlng.getLng(), latlng.getLat(), function(result, status) {
                        if (status === kakao.maps.services.Status.OK) {
                            var roadAddr = result[0].road_address ? result[0].road_address.address_name : '';  // 도로명 주소
                            var jibunAddr = result[0].address.address_name; // 지번 주소

                            // 기본 주소 세팅
                            selectedAddress = roadAddr ? roadAddr : jibunAddr;

                            // 장소 정보 검색 (역명, 건물명 등)
                            ps.keywordSearch(selectedAddress, function(data, status) {
                                if (status === kakao.maps.services.Status.OK && data.length > 0) {
                                    // 가장 첫 번째 결과(역명, 건물명 등)를 주소와 함께 표시
                                    selectedAddress = data[0].place_name + ' (' + selectedAddress + ')';
                                }

                                // 모달 내 주소 표시
                                document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                            });

                            // 마커 위치 변경
                            marker.setPosition(latlng);
                            lastCoords = latlng; // 마지막 클릭한 좌표를 저장
                        }
                    });
                });
            } else {
                // 지도 객체가 이미 존재할 경우, 검색된 좌표가 있으면 그 좌표로 설정
                if (lastCoords) {
                    map.setCenter(lastCoords); // 마지막 검색된 좌표로 중심 설정
                }
                map.relayout(); // 지도의 크기를 재조정
            }
        });

        // 위치 검색 버튼 클릭 시
        document.getElementById('searchLocation').onclick = function() {
            var location = document.getElementById('locationTag').value.trim(); // 공백 제거

            if (location === '') {
                alert('검색할 주소를 입력해주세요.');
                return;
            }

            var geocoder = new kakao.maps.services.Geocoder();

            // 주소 검색
            geocoder.addressSearch(location, function(result, status) {
                if (status === kakao.maps.services.Status.OK) {
                    if (result && result.length > 0) {
                        var coords = new kakao.maps.LatLng(result[0].y, result[0].x);

                        // 지도 객체가 초기화되지 않았을 경우 처리
                        if (map) {
                            map.setCenter(coords);  // 지도 중심 설정
                            marker.setPosition(coords);  // 마커 위치 설정
                        }

                        // 마지막 검색된 좌표 저장
                        lastCoords = coords;

                        // 모달 열기
                        var mapModal = new bootstrap.Modal(document.getElementById('mapModal'));
                        mapModal.show();
                    } else {
                        alert('검색 결과가 없습니다.');
                    }
                } else {
                    alert('주소를 찾을 수 없습니다.');
                }
            });
        };

        // 선택 버튼 클릭 시 입력창에 주소 반영
        document.getElementById('confirmAddress').onclick = function() {
            if (selectedAddress) {
                document.getElementById('locationTag').value = selectedAddress;
            }
            var mapModal = bootstrap.Modal.getInstance(document.getElementById('mapModal'));
            mapModal.hide(); // 모달 닫기
        };
    } else {
        console.error("Kakao Maps API is not loaded.");
    }
};
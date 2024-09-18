window.onload = function() {
    if (typeof kakao !== 'undefined') {
        var map = null; // 지도 객체 초기화
        var marker = null; // 마커 객체 초기화
        var geocoder = new kakao.maps.services.Geocoder(); // 주소 검색을 위한 geocoder 객체
        var ps = new kakao.maps.services.Places(); // 장소 검색을 위한 Places 객체
        var selectedAddress = ''; // 선택된 주소 저장 변수
        var mapInitialized = false; // 지도 초기화 여부

        // 사용자의 현재 위치를 가져와 지도에 반영
        function updateMapWithCurrentLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    var lat = position.coords.latitude; // 위도
                    var lng = position.coords.longitude; // 경도
                    var currentCoords = new kakao.maps.LatLng(lat, lng); // 사용자의 현재 좌표

                    // 현재 위치를 지도 중심으로 설정
                    map.setCenter(currentCoords);
                    marker.setPosition(currentCoords);
                    marker.setMap(map);

                    // 좌표를 주소로 변환하여 결과 표시
                    geocoder.coord2Address(lng, lat, function(result, status) {
                        if (status === kakao.maps.services.Status.OK) {
                            var roadAddr = result[0].road_address ? result[0].road_address.address_name : '';  // 도로명 주소
                            var jibunAddr = result[0].address.address_name; // 지번 주소
                            selectedAddress = roadAddr ? roadAddr : jibunAddr;

                            // 장소 검색 (건물명, 역명 등)
                            ps.keywordSearch(selectedAddress, function(data, status) {
                                if (status === kakao.maps.services.Status.OK && data.length > 0) {
                                    selectedAddress = data[0].place_name + ' (' + selectedAddress + ')';
                                }

                                // 모달 내부에 선택된 주소 표시
                                document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                                document.getElementById('locationTag').value = selectedAddress; // 선택된 주소를 숨겨진 필드에 저장
                                document.getElementById('locationDisplay').value = selectedAddress; // 선택된 주소를 표시
                            });
                        }
                    });
                }, function(error) {
                    console.error("Geolocation failed: " + error.message);
                });
            } else {
                alert("Geolocation을 지원하지 않는 브라우저입니다.");
            }
        }

        // 지도에서 위치 선택 버튼 클릭 시 모달 열기 및 지도 초기화
        document.getElementById('openMapModal').onclick = function() {
            var mapModal = new bootstrap.Modal(document.getElementById('mapModal'));
            mapModal.show();

            if (!mapInitialized) {
                var mapContainer = document.getElementById('map');
                var mapOption = {
                    center: new kakao.maps.LatLng(33.450701, 126.570667), // 초기 지도 중심 좌표 설정
                    level: 3 // 초기 지도 확대 레벨 설정
                };

                map = new kakao.maps.Map(mapContainer, mapOption); // 지도 객체 생성
                marker = new kakao.maps.Marker(); // 마커 객체 생성
                mapInitialized = true; // 지도 초기화 완료
            }

            // 모달이 열릴 때 현재 위치로 지도 업데이트
            updateMapWithCurrentLocation();
        };

        // 지도 클릭 시 클릭한 좌표의 주소를 찾아서 선택
        document.getElementById('mapModal').addEventListener('shown.bs.modal', function () {
            kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
                var latlng = mouseEvent.latLng;
                geocoder.coord2Address(latlng.getLng(), latlng.getLat(), function(result, status) {
                    if (status === kakao.maps.services.Status.OK) {
                        var roadAddr = result[0].road_address ? result[0].road_address.address_name : '';  // 도로명 주소
                        var jibunAddr = result[0].address.address_name; // 지번 주소

                        selectedAddress = roadAddr ? roadAddr : jibunAddr;
                        document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                        document.getElementById('locationTag').value = selectedAddress; // 선택된 주소 저장
                        document.getElementById('locationDisplay').value = selectedAddress; // 선택된 주소 표시

                        // 마커 위치 변경
                        marker.setPosition(latlng);
                        marker.setMap(map);

                        // 건물명, 역명 등 검색 추가
                        ps.keywordSearch(selectedAddress, function(data, status) {
                            if (status === kakao.maps.services.Status.OK && data.length > 0) {
                                selectedAddress = data[0].place_name + ' (' + selectedAddress + ')';
                                document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                                document.getElementById('locationTag').value = selectedAddress; // 선택된 주소 저장
                                document.getElementById('locationDisplay').value = selectedAddress; // 선택된 주소 표시
                            }
                        });
                    } else {
                        alert('주소를 찾을 수 없습니다.');
                    }
                });
            });
        });

        // 선택 버튼 클릭 시 주소를 폼에 반영
        document.getElementById('confirmAddress').onclick = function() {
            if (selectedAddress) {
                document.getElementById('locationTag').value = selectedAddress; // 선택된 주소를 숨겨진 필드에 저장
                document.getElementById('locationDisplay').value = selectedAddress; // 선택된 주소를 표시
            }
            var mapModal = bootstrap.Modal.getInstance(document.getElementById('mapModal'));
            mapModal.hide(); // 모달 닫기
        };

        // 모달이 열릴 때 현재 위치로 지도 초기화
        document.getElementById('mapModal').addEventListener('shown.bs.modal', function () {
            updateMapWithCurrentLocation(); // 현재 위치로 지도 업데이트
        });
    } else {
        console.error("Kakao Maps API is not loaded.");
    }
};
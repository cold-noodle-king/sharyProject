window.onload = function() {
    if (typeof kakao !== 'undefined') {
        var map = null; // 지도 객체
        var marker = null; // 마커 객체
        var geocoder = new kakao.maps.services.Geocoder(); // 주소 변환 객체
        var ps = new kakao.maps.services.Places(); // 장소 검색 객체
        var selectedAddress = ''; // 선택된 주소

        // 지도 초기화 함수 (모달이 열릴 때 호출)
        function initMap() {
            var mapContainer = document.getElementById('map');
            var mapOption = {
                center: new kakao.maps.LatLng(33.450701, 126.570667),
                level: 3
            };

            map = new kakao.maps.Map(mapContainer, mapOption); // 지도 생성
            marker = new kakao.maps.Marker(); // 마커 생성
        }

        // 현재 위치로 지도 업데이트
        function updateMapWithCurrentLocation() {
            if (navigator.geolocation) {
                navigator.geolocation.getCurrentPosition(function(position) {
                    var lat = position.coords.latitude;
                    var lng = position.coords.longitude;
                    var currentCoords = new kakao.maps.LatLng(lat, lng);

                    map.setCenter(currentCoords); // 현재 위치로 지도 중심 설정
                    marker.setPosition(currentCoords); // 마커 위치 설정
                    marker.setMap(map); // 지도에 마커 표시

                    // 좌표를 주소로 변환
                    geocoder.coord2Address(lng, lat, function(result, status) {
                        if (status === kakao.maps.services.Status.OK) {
                            var roadAddr = result[0].road_address ? result[0].road_address.address_name : '';
                            var jibunAddr = result[0].address.address_name;
                            selectedAddress = roadAddr ? roadAddr : jibunAddr;

                            ps.keywordSearch(selectedAddress, function(data, status) {
                                if (status === kakao.maps.services.Status.OK && data.length > 0) {
                                    selectedAddress = data[0].place_name + ' (' + selectedAddress + ')';
                                }

                                // 선택된 주소 표시
                                document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                                document.getElementById('locationTag').value = selectedAddress; // 숨겨진 필드에 저장
                                document.getElementById('locationDisplay').value = selectedAddress; // 화면에 표시
                            });
                        }
                    });
                });
            } else {
                alert("Geolocation을 지원하지 않는 브라우저입니다.");
            }
        }

        // 모달이 열릴 때 지도 초기화 및 현재 위치로 업데이트
        document.getElementById('mapModal').addEventListener('shown.bs.modal', function () {
            if (!map) { // 지도 객체가 없을 경우에만 초기화
                initMap(); // 지도 초기화
            }
            updateMapWithCurrentLocation(); // 현재 위치로 지도 업데이트

            // 지도 클릭 이벤트 설정
            kakao.maps.event.addListener(map, 'click', function(mouseEvent) {
                var latlng = mouseEvent.latLng;
                geocoder.coord2Address(latlng.getLng(), latlng.getLat(), function(result, status) {
                    if (status === kakao.maps.services.Status.OK) {
                        var roadAddr = result[0].road_address ? result[0].road_address.address_name : '';
                        var jibunAddr = result[0].address.address_name;

                        selectedAddress = roadAddr ? roadAddr : jibunAddr;
                        document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                        document.getElementById('locationTag').value = selectedAddress;
                        document.getElementById('locationDisplay').value = selectedAddress;

                        marker.setPosition(latlng); // 마커 위치 변경
                        marker.setMap(map); // 지도에 마커 표시

                        ps.keywordSearch(selectedAddress, function(data, status) {
                            if (status === kakao.maps.services.Status.OK && data.length > 0) {
                                selectedAddress = data[0].place_name + ' (' + selectedAddress + ')';
                                document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                                document.getElementById('locationTag').value = selectedAddress;
                                document.getElementById('locationDisplay').value = selectedAddress;
                            }
                        });
                    } else {
                        alert('주소를 찾을 수 없습니다.');
                    }
                });
            });
        });

        // 위치 선택 버튼 클릭 시 모달 열기
        document.getElementById('openMapModal').onclick = function() {
            var mapModal = new bootstrap.Modal(document.getElementById('mapModal'));
            mapModal.show();
        };

        // 선택 버튼 클릭 시 주소 반영
        document.getElementById('confirmAddress').onclick = function() {
            if (selectedAddress) {
                document.getElementById('locationTag').value = selectedAddress;
                document.getElementById('locationDisplay').value = selectedAddress;
            }
            var mapModal = bootstrap.Modal.getInstance(document.getElementById('mapModal'));
            mapModal.hide();
        };
    } else {
        console.error("Kakao Maps API가 로드되지 않았습니다.");
    }
};

document.addEventListener("DOMContentLoaded", function() {
    if (typeof kakao !== 'undefined' && kakao.maps) {
        // Kakao API 로드 이후 실행
        kakao.maps.load(function() {
            var map = null;
            var marker = null;
            var geocoder = new kakao.maps.services.Geocoder();
            var ps = new kakao.maps.services.Places();
            var selectedAddress = '';

            // 지도 초기화 함수
            function initMap() {
                var mapContainer = document.getElementById('map');
                var mapOption = {
                    center: new kakao.maps.LatLng(33.450701, 126.570667),
                    level: 3
                };
                map = new kakao.maps.Map(mapContainer, mapOption);
                marker = new kakao.maps.Marker();
            }

            // 현재 위치로 지도 업데이트
            function updateMapWithCurrentLocation() {
                if (navigator.geolocation) {
                    navigator.geolocation.getCurrentPosition(function(position) {
                        var lat = position.coords.latitude;
                        var lng = position.coords.longitude;
                        var currentCoords = new kakao.maps.LatLng(lat, lng);
                        map.setCenter(currentCoords);
                        marker.setPosition(currentCoords);
                        marker.setMap(map);

                        geocoder.coord2Address(lng, lat, function(result, status) {
                            if (status === kakao.maps.services.Status.OK) {
                                var roadAddr = result[0].road_address ? result[0].road_address.address_name : '';
                                var jibunAddr = result[0].address.address_name;
                                selectedAddress = roadAddr ? roadAddr : jibunAddr;
                                ps.keywordSearch(selectedAddress, function(data, status) {
                                    if (status === kakao.maps.services.Status.OK && data.length > 0) {
                                        selectedAddress = data[0].place_name + ' (' + selectedAddress + ')';
                                    }
                                    document.getElementById('selectedAddress').textContent = '선택된 주소: ' + selectedAddress;
                                    document.getElementById('locationTag').value = selectedAddress;
                                    document.getElementById('locationDisplay').value = selectedAddress;
                                });
                            }
                        });
                    });
                } else {
                    alert("Geolocation을 지원하지 않는 브라우저입니다.");
                }
            }

            // 지도 클릭 이벤트 설정 등 나머지 로직...
            document.getElementById('mapModal').addEventListener('shown.bs.modal', function () {
                if (!map) {
                    initMap();
                }
                updateMapWithCurrentLocation();
            });

            document.getElementById('openMapModal').onclick = function() {
                var mapModal = new bootstrap.Modal(document.getElementById('mapModal'));
                mapModal.show();
            };

            document.getElementById('confirmAddress').onclick = function() {
                if (selectedAddress) {
                    document.getElementById('locationTag').value = selectedAddress;
                    document.getElementById('locationDisplay').value = selectedAddress;
                }
                var mapModal = bootstrap.Modal.getInstance(document.getElementById('mapModal'));
                mapModal.hide();
            };
        });
    } else {
        console.error("Kakao Maps API가 로드되지 않았습니다.");
    }
});

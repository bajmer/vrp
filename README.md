Program rozwiązuje problem marszrutyzacji z ograniczeniami ładowności pojazdów (CVRP) oraz oknami czasowymi (VRPTW). Przed uruchomieniem aplikacji należy w pierwszej kolejności uruchomić lokalnie open source'owy serwer routingowy, który wyznacza odległości oraz czasy przejazdu między lokalizacjami. 

Aby uruchomić serwer routingowy należy skorzystać z obrazu dockerowego wykonując w konsoli następujące polecenia:
  1) wget http://download.geofabrik.de/europe/poland/mazowieckie-latest.osm.pbf (pobranie mapy województwa mazowieckiego)
  2) docker run -t -v $(pwd):/data osrm/osrm-backend osrm-extract -p /opt/car.lua /data/mazowieckie-latest.osm.pbf
  3) docker run -t -v $(pwd):/data osrm/osrm-backend osrm-contract /data/mazowieckie-latest.osrm
  4) docker run -t -i -p 5000:5000 -v $(pwd):/data osrm/osrm-backend osrm-routed /data/berlin-latest.osrm (uruchomienie serwera na porcie 5000.
  
Po uruchomieniu programu należy:
  1) Wcisnąć przycisk "Load customers", w celu wczytania klientów (uwaga - pierwsza pozycja na liście to magazyn, czyli punkt startowy dla pojazdów). Przykładowa lokalizacja pliku z klientami: /input/Customers_15.csv
  Po jego wybraniu w oknie "Customers" pojawi się lista klientów. Kliknięcie w klienta na liście spowoduje wyświetlenie jego położenia na mapie.
  2) Wcisnąć przycisk "Get distance matrix" - w tym momencie serwer routingowy wysyła do programu odległości między każdą parą klientów.
  3) W oknie "General parameters" ustawić suwakami ładowność pojazdu w kg oraz pojemność w m3.
  4) Z listy "Algorithm type" wybieramy rodzaj algorytmu (jeśli wybierzemy ACS, wówczas w oknie "ACS parameters" należy suwakami ustawić parametry algorytmu)
  5) Wcisnąć przycisk "Find solution", aby wyznaczyć rozwiązanie problemu. W oknie "Solutions" zostanie wyświetlone rozwijalne drzewo, do którego dołączane będą kolejne rozwiązania. Drzewo ma 3 poziomy:
      - solution - rozwiązanie problemu, kliknięcie spowoduje wyświetlenie całego rozwiązania na mapie
      - route - trasa jednego pojazdu należąca do rozwiązania, kliknięcie spowoduje wyświetlenie danej trasy na mapie oraz            wyświetlenie listy odcinków trasy w oknie "Route details"
      - route segment - odcinek trasy należący do trasy, kliknięcie spowoduje wyświetlenie odcinka trasy na mapie.

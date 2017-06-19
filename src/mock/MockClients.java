//package mock;
//
//import project.Client;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by mbala on 07.06.17.
// */
//public class MockClients {
//
//    private List<Client> listOfClients;
//
//    public MockClients() {
//        listOfClients = new ArrayList<>();
//        listOfClients.add(new Client(52.50953477032727, 20.994873046875));
//        listOfClients.add(new Client(52.14697334064471, 22.313232421875));
//
//        listOfClients.add(new Client(52.696361078274485, 22.30224609375));
//
//        listOfClients.add(new Client(52.82932091031374, 21.346435546875));
//
//        listOfClients.add(new Client(52.74959372674114, 20.93994140625));
//
//        listOfClients.add(new Client(52.82932091031374, 20.159912109375));
//
//        listOfClients.add(new Client(52.5897007687178, 21.346435546875));
//
//        listOfClients.add(new Client(52.44261787120724, 21.785888671875));
//
//        listOfClients.add(new Client(52.0862573323384, 21.4013671875));
//
//        listOfClients.add(new Client(51.984880139916626, 21.07177734375));
//
//        listOfClients.add(new Client(52.348763181988105, 20.3466796875));
//
//        listOfClients.add(new Client(52.54963607438228, 19.70947265625));
//
//        listOfClients.add(new Client(53.05442186546102, 20.76416015625));
//
//        listOfClients.add(new Client(53.23892064092497, 21.346435546875));
//
//        listOfClients.add(new Client(51.385495069223204, 21.126708984375));
//
//        listOfClients.add(new Client(51.44716034698012, 20.731201171875));
//
//        listOfClients.add(new Client(51.81540697949439, 20.819091796875));
//
//        listOfClients.add(new Client(51.699799849741936, 21.24755859375));
//
//        listOfClients.add(new Client(51.92394344554469, 21.5771484375));
//
//        listOfClients.add(new Client(52.30176096373671, 21.829833984375));
//
//        listOfClients.add(new Client(52.1267438596429, 20.621337890625));
//
//        listOfClients.add(new Client(52.48947038534306, 21.29150390625));
//
//        listOfClients.add(new Client(52.562995039558004, 20.7421875));
//
//        listOfClients.add(new Client(52.54963607438228, 20.577392578125));
//
//        listOfClients.add(new Client(52.696361078274485, 20.21484375));
//
//        listOfClients.add(new Client(52.68970242806751, 21.829833984375));
//
//        listOfClients.add(new Client(52.50284765940397, 22.1923828125));
//
//        listOfClients.add(new Client(52.36721467920585, 21.170654296875));
//
//        listOfClients.add(new Client(52.300081389496114, 21.58538818359375));
//
//        listOfClients.add(new Client(52.5897007687178, 21.4947509765625));
//
//        listOfClients.add(new Client(52.53627304145948, 21.69525146484375));
//
//        listOfClients.add(new Client(52.80442185934101, 21.895751953125));
//
//        listOfClients.add(new Client(53.08412692217884, 21.5496826171875));
//
//        listOfClients.add(new Client(52.9999093684334, 20.906982421875));
//
//        listOfClients.add(new Client(52.87410326334818, 20.6378173828125));
//
//        listOfClients.add(new Client(53.065976266616026, 19.90447998046875));
//
//        listOfClients.add(new Client(52.8459123539017, 19.654541015625));
//
//        listOfClients.add(new Client(52.73961755427091, 19.775390625));
//
//        listOfClients.add(new Client(52.4107966877885, 19.45953369140625));
//
//        listOfClients.add(new Client(52.52123476655549, 20.05828857421875));
//
//        listOfClients.add(new Client(52.22275209302143, 20.23681640625));
//
//        listOfClients.add(new Client(52.20928975234919, 20.42083740234375));
//
//        listOfClients.add(new Client(52.234528294213646, 20.98388671875));
//
//        listOfClients.add(new Client(52.079506003796965, 21.005859375));
//
//        listOfClients.add(new Client(51.68617954855625, 20.7366943359375));
//
//        listOfClients.add(new Client(51.77803705914518, 21.21734619140625));
//
//        listOfClients.add(new Client(51.786532942396384, 21.57440185546875));
//
//        listOfClients.add(new Client(52.2059235296225, 22.5164794921875));
//
//        listOfClients.add(new Client(52.19750685699391, 22.74444580078125));
//
//        listOfClients.add(new Client(52.20424032262009, 21.56890869140625));
//
//        listOfClients.add(new Client(52.407445753192064, 22.00836181640625));
//
//        listOfClients.add(new Client(51.61119461048402, 20.52520751953125));
//
//        listOfClients.add(new Client(51.50532341149336, 21.10748291015625));
//
//        listOfClients.add(new Client(51.5463350479341, 20.8740234375));
//
//        listOfClients.add(new Client(51.64018076739535, 20.97015380859375));
//
//        listOfClients.add(new Client(51.14661735383552, 21.65679931640625));
//
//        listOfClients.add(new Client(51.354631230360226, 21.59088134765625));
//
//        listOfClients.add(new Client(51.30829645782522, 21.24481201171875));
//
//        listOfClients.add(new Client(51.36320660581432, 20.62957763671875));
//
//        listOfClients.add(new Client(52.440943644738674, 20.663909912109375));
//
//        listOfClients.add(new Client(52.40912125231122, 20.9564208984375));
//
//        listOfClients.add(new Client(52.30763897191189, 21.1541748046875));
//
//        listOfClients.add(new Client(52.28412225676033, 21.106109619140625));
//
//        listOfClients.add(new Client(52.09638241034154, 21.26129150390625));
//
//        listOfClients.add(new Client(52.173089491230904, 20.812225341796875));
//
//        listOfClients.add(new Client(52.13180209119314, 20.732574462890625));
//
//        listOfClients.add(new Client(51.97980524492489, 20.5169677734375));
//
//        listOfClients.add(new Client(51.97557572654266, 21.207733154296875));
//
//        listOfClients.add(new Client(52.11325243469631, 21.9122314453125));
//
//        listOfClients.add(new Client(52.72298552457069, 21.2091064453125));
//
//    }
//
//    public List<Client> getListOfClients() {
//        return listOfClients;
//    }
//}
//
//
//
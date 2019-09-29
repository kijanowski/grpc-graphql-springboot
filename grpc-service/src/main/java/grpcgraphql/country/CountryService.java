package grpcgraphql.country;

import com.google.protobuf.ProtocolStringList;
import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@GRpcService
public class CountryService extends CountryServiceGrpc.CountryServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(CountryService.class);

    private static final Map<String, String> ALL_COUNTRIES = Map.of(
      "A", "Australia",
      "B", "Brazil",
      "C", "Chile",
      "D", "Denmark",
      "E", "Estonia",
      "F", "Fiji"
    );

    @Override
    public void getCountries(Country.CountryRequest request, StreamObserver<Country.CountriesReply> responseObserver) {
        ProtocolStringList ids = request.getIdList();

        final Country.CountriesReply.Builder replyBuilder =
          Country.CountriesReply.newBuilder();

        ids.forEach(id -> {
            LOG.info("================> Adding country with id {}", id);
            replyBuilder.addCountry(ALL_COUNTRIES.get(id));
        });

        responseObserver.onNext(replyBuilder.build());
        responseObserver.onCompleted();
        LOG.info("================> getCountries done");
    }

}

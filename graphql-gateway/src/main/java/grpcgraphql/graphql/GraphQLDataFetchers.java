package grpcgraphql.graphql;

import graphql.schema.DataFetcher;
import grpcgraphql.animal.AnimalOuterClass;
import grpcgraphql.animal.AnimalServiceGrpc;
import grpcgraphql.country.Country;
import grpcgraphql.country.CountryServiceGrpc;
import grpcgraphql.graphql.model.AnimalTO;
import grpcgraphql.graphql.model.CountryTO;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.dataloader.BatchLoader;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class GraphQLDataFetchers {

    private static final Logger LOG = LoggerFactory.getLogger(GraphQLDataFetchers.class);

    private final CountryServiceGrpc.CountryServiceFutureStub countryServiceStub;
    private final AnimalServiceGrpc.AnimalServiceFutureStub animalServiceStub;

    public GraphQLDataFetchers() {
        ManagedChannel animalChannel = ManagedChannelBuilder
          .forAddress("localhost", 6568)
          .usePlaintext()
          .build();

        ManagedChannel countryChannel = ManagedChannelBuilder
          .forAddress("localhost", 6569)
          .usePlaintext()
          .build();

        this.animalServiceStub = AnimalServiceGrpc.newFutureStub(animalChannel);
        this.countryServiceStub = CountryServiceGrpc.newFutureStub(countryChannel);
    }

    public DataFetcher animalsFetcher() {
        return dataFetchingEnvironment -> {
            Optional<String> animalId = Optional.ofNullable(dataFetchingEnvironment.getArgument("id"));
            try {
                AnimalOuterClass.AnimalRequest.Builder requestBuilder = AnimalOuterClass.AnimalRequest.newBuilder();
                animalId.ifPresent(requestBuilder::setId);
                AnimalOuterClass.AnimalRequest request = requestBuilder.build();

                List<AnimalOuterClass.Animal> animalList = animalServiceStub
                  .getAnimals(request)
                  .get()
                  .getAnimalList();

                List<AnimalTO> collect = animalList.stream().map(res ->
                  new AnimalTO(
                    res.getName(),
                    res.getName(),
                    res.getColor(),
                    res.getCountryList(),
                    null
                  )
                ).collect(Collectors.toList());
                LOG.info("Found animals: {}", collect.size());
                return collect;
            } catch (Exception exc) {
                LOG.error("Failed to fetch animals: ", exc);
                return null;
            }
        };
    }

    public DataFetcher animalCountriesFetcher() {
        return dataFetchingEnvironment -> {
            AnimalTO animal = dataFetchingEnvironment.getSource();
            Collection<String> countryIds = animal.getCountryIds();

            DataLoaderRegistry dataLoaderRegistry = dataFetchingEnvironment.getContext();
            DataLoader<String, CountryTO> countryLoader = dataLoaderRegistry.getDataLoader("countries");

            return countryLoader
              .loadMany(new ArrayList<>(countryIds))
              .exceptionally(exc -> {
                  LOG.error("Failed to load countries with exception: ", exc);
                  return null;
              });
        };
    }

    public BatchLoader<String, CountryTO> countryBatchLoader() {
        return countryIds ->
          CompletableFuture.supplyAsync(() -> {
                try {
                    List<String> countryList = countryServiceStub
                      .getCountries(Country.CountryRequest.newBuilder().addAllId(countryIds).build())
                      .get()
                      .getCountryList();

                    List<CountryTO> collect = countryList
                      .stream()
                      .map(res -> new CountryTO(res, res))
                      .collect(Collectors.toList());
                    LOG.info("Found countries: {}", collect.size());
                    return collect;
                } catch (Exception exc) {
                    LOG.error("Failed to fetch countries: ", exc);
                    return null;
                }
            }
          );
    }
}

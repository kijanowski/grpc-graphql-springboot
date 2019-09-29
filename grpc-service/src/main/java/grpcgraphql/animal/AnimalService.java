package grpcgraphql.animal;

import io.grpc.stub.StreamObserver;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@GRpcService
public class AnimalService extends AnimalServiceGrpc.AnimalServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(AnimalService.class);

    private static final List<AnimalOuterClass.Animal> ALL_ANIMALS = List.of(
      AnimalOuterClass.Animal.newBuilder()
        .setName("Pulpo")
        .setColor("pink")
        .addAllCountry(List.of("A", "F"))
        .build(),
      AnimalOuterClass.Animal.newBuilder()
        .setName("Croco")
        .setColor("green")
        .addAllCountry(List.of("A", "B", "F"))
        .build(),
      AnimalOuterClass.Animal.newBuilder()
        .setName("Gato")
        .setColor("black")
        .addCountry("E")
        .build(),
      AnimalOuterClass.Animal.newBuilder()
        .setName("Chicko")
        .setColor("yellow")
        .addAllCountry(List.of("A", "B", "C", "D"))
        .build()
    );

    @Override
    public void getAnimals(AnimalOuterClass.AnimalRequest request, StreamObserver<AnimalOuterClass.AnimalsReply> responseObserver) {
        final AnimalOuterClass.AnimalsReply.Builder replyBuilder =
          AnimalOuterClass.AnimalsReply.newBuilder();

        String animalId = request.getId();
        if (animalId == null || "".equals(animalId)) {
            replyBuilder.addAllAnimal(ALL_ANIMALS);
        } else {
            replyBuilder.addAnimal(ALL_ANIMALS.get(Integer.parseInt(animalId)));
        }

        responseObserver.onNext(replyBuilder.build());
        responseObserver.onCompleted();
        LOG.info("================> getAnimals done for id: {}", animalId);
    }

}

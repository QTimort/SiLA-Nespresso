package fr.diguiet.nespresso.server;

import fr.diguiet.nespresso.server.nespresso.NespressoUtils;
import fr.diguiet.nespresso.server.nespresso.model.Capsule;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import sila2.fr.diguiet.misc.nespresso.v1.NespressoGrpc;
import sila2.fr.diguiet.misc.nespresso.v1.NespressoOuterClass;
import sila2.org.silastandard.SiLAFramework;
import sila_java.library.core.utils.SiLAErrors;

import java.util.*;
import java.util.stream.Collectors;

public class NespressoRpcImpl extends NespressoGrpc.NespressoImplBase {
    private final Map<String, Long> shoppingList = new HashMap<>();
    private final Map<String, Capsule> capsules = NespressoUtils.getAvailableCoffees();

    @Override
    public void addCoffees(
            final NespressoOuterClass.AddCoffees_Parameters request,
            final StreamObserver<NespressoOuterClass.AddCoffees_Responses> responseObserver
    ) {
        final List<String> coffees = request.getCoffeesList()
                .stream()
                .map(SiLAFramework.String::getValue)
                .collect(Collectors.toList());
        final Set<String> invalidCoffees = coffees
                .stream()
                .filter(c -> !this.capsules.containsKey(c))
                .collect(Collectors.toSet());
        if (!invalidCoffees.isEmpty()) {
            throw SiLAErrors.generateValidationError(
                    "Coffees",
                    "Unknown coffees: " + invalidCoffees,
                    "Specify valid coffees");
        }
        coffees.forEach(coffee -> shoppingList.put(coffee, shoppingList.getOrDefault(coffee, 0L) + 1));
        responseObserver.onNext(NespressoOuterClass.AddCoffees_Responses.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeCoffee(
            final NespressoOuterClass.RemoveCoffee_Parameters request,
            final StreamObserver<NespressoOuterClass.RemoveCoffee_Responses> responseObserver
    ) {
        final String coffee = request.getCoffee().getName().getValue();
        final long quantity = request.getCoffee().getQuantity().getValue();

        shoppingList.computeIfPresent(coffee, (k, v) -> (v > quantity) ? (v - quantity) : (shoppingList.remove(k)));

        responseObserver.onNext(NespressoOuterClass.RemoveCoffee_Responses.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void getCoffees(
            final NespressoOuterClass.Get_Coffees_Parameters request,
            final StreamObserver<NespressoOuterClass.Get_Coffees_Responses> responseObserver) {
        final List<NespressoOuterClass.Get_Coffees_Responses.Coffees_Struct> coffeeListStructs =
                this.capsules
                        .values()
                        .stream()
                        .map(NespressoRpcImpl::capsuleToCoffeesStruct)
                        .collect(Collectors.toList());
        responseObserver.onNext(NespressoOuterClass.Get_Coffees_Responses
                .newBuilder()
                .addAllCoffees(coffeeListStructs)
                .build()
        );
        responseObserver.onCompleted();
    }

    @Override
    public void getShoppingList(
            final NespressoOuterClass.Get_ShoppingList_Parameters request,
            final StreamObserver<NespressoOuterClass.Get_ShoppingList_Responses> responseObserver
    ) {
        final List<NespressoOuterClass.Get_ShoppingList_Responses.ShoppingList_Struct> shoppingListStructs =
                this.shoppingList
                .entrySet()
                .stream()
                .map((e) ->
                        NespressoOuterClass.Get_ShoppingList_Responses.ShoppingList_Struct
                                .newBuilder()
                                .setName(SiLAFramework.String.newBuilder().setValue(e.getKey()).build())
                                .setQuantity(SiLAFramework.Integer.newBuilder().setValue(e.getValue()).build())
                                .build()
                ).collect(Collectors.toList());
        responseObserver.onNext(NespressoOuterClass.Get_ShoppingList_Responses
                .newBuilder()
                .addAllShoppingList(shoppingListStructs)
                .build()
        );
        responseObserver.onCompleted();
    }

    private static NespressoOuterClass.Get_Coffees_Responses.Coffees_Struct capsuleToCoffeesStruct(
            @NonNull final Capsule capsule
    ) {
        return NespressoOuterClass.Get_Coffees_Responses.Coffees_Struct
                .newBuilder()
                .setName(SiLAFramework.String.newBuilder().setValue(capsule.getName()).build())
                .setCupSize(SiLAFramework.String.newBuilder().setValue(capsule.getCupSizes().toString()).build())
                .setDescription(SiLAFramework.String.newBuilder().setValue(capsule.getHeadline()).build())
                .setIntensity(SiLAFramework.Integer.newBuilder().setValue(capsule.getIntensity()).build())
                .build();
    }
}

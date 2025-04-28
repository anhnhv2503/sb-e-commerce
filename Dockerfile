FROM openjdk:21
EXPOSE 8080
ADD /target/spring-shopping-cart-0.0.1-SNAPSHOT.jar spring-shopping-cart.jar
ENTRYPOINT ["java", "-jar", "spring-shopping-cart.jar"]
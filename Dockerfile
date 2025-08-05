FROM public.ecr.aws/docker/library/gradle:jdk21-alpine AS build
WORKDIR /app
COPY . .
RUN gradle build

FROM public.ecr.aws/amazoncorretto/amazoncorretto:21
WORKDIR /app
COPY --from=build /app/build/libs/unitune-0.0.1-SNAPSHOT.jar unitune-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "unitune-0.0.1-SNAPSHOT.jar"]
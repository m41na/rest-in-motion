package works.hop.withservlet;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.schema.GraphQLSchema;
import graphql.servlet.SimpleGraphQLServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import works.hop.withservlet.demo.service.AttendeeService;
import works.hop.withservlet.demo.service.SpeakerService;
import works.hop.withservlet.demo.service.TalkService;
import works.hop.withservlet.resolver.Mutation;
import works.hop.withservlet.resolver.Query;
import works.hop.withservlet.resolver.TalkSpeakerResolver;

@SpringBootApplication
public class DemoWithServletApp {

    @Autowired
    private SpeakerService speakerService;
    @Autowired
    private TalkService talkService;
    @Autowired
    private AttendeeService attendeeService;

    public static void main(String[] args) {
        SpringApplication.run(DemoWithServletApp.class, args);
    }

    private GraphQLSchema buildSchema() {
        return SchemaParser
                .newParser()
                .file("schema/schema.graphql")
                .resolvers(
                        new Query(talkService, speakerService, attendeeService),
                        new Mutation(talkService, speakerService, attendeeService),
                        new TalkSpeakerResolver(speakerService)
                )
                .build()
                .makeExecutableSchema();
    }

    @Bean
    public ServletRegistrationBean graphQLServlet() {
        return new ServletRegistrationBean(new SimpleGraphQLServlet(buildSchema()), "/graphql");
    }
}

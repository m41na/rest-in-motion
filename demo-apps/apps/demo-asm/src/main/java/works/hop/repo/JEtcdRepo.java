package works.hop.repo;

import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class JEtcdRepo {

    private static JEtcdRepo instance;

    private final Client client;

    private JEtcdRepo() {
        this.client = Client.builder().endpoints("http://localhost:2379").build();
        init();
    }

    public static JEtcdRepo instance() {
        if (instance == null) {
            instance = new JEtcdRepo();
        }
        return instance;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        try(KV client = instance().kvClient()) {
            ByteSequence key = ByteSequence.from("firstName".getBytes());
            ByteSequence value = ByteSequence.from("Steve".getBytes());
            CompletableFuture<PutResponse> putResult = client.put(key, value);
            putResult.thenAccept(res -> System.out.println(res));
            CompletableFuture<GetResponse> getResult = client.get(key);
            getResult.thenAccept(res -> System.out.println(res));
            client.delete(key);
        }
    }

    private void init() {
    }

    public KV kvClient() {
        return client.getKVClient();
    }

    public Lease leaseClient() {
        return client.getLeaseClient();
    }

    public Lock lockClient() {
        return client.getLockClient();
    }
}

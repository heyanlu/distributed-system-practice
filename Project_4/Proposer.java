package Project_4;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Proposer {
    private AtomicInteger proposalCounter;
    private List<Acceptor> acceptors;
    private Learner learner;
    private final int serverId;
    private static final int MAX_RETRIES = 5;
    private static final AtomicInteger globalCounter = new AtomicInteger(1000000); // Shared counter for all proposers

    public Proposer(List<Acceptor> acceptors, Learner learner) {
        this(0, acceptors, learner);
    }

    public Proposer(int serverId, List<Acceptor> acceptors, Learner learner) {
        this.serverId = serverId;
        this.proposalCounter = new AtomicInteger(serverId * 100); // Start with different base values
        this.acceptors = acceptors;
        this.learner = learner;
    }


    private int generateProposalNumber() {
        int proposalNum = globalCounter.incrementAndGet();

        int highestPromised = 0;
        for (Acceptor acceptor : acceptors) {
            if (!acceptor.isFailed()) {
                try {
                    int promised = acceptor.getHighestPromisedProposalNumber();
                    if (promised > highestPromised) {
                        highestPromised = promised;
                    }
                } catch (Exception e) {

                }
            }
        }

        if (proposalNum <= highestPromised) {
            proposalNum = highestPromised + 10;
            globalCounter.set(proposalNum);
        }

        return proposalNum;
    }

    public String propose(String key, String value, String operation) {
        int retries = MAX_RETRIES;
        while (retries > 0) {
            int proposalNumber = generateProposalNumber();
            System.out.println("Proposer " + serverId + ": Starting proposal " + proposalNumber +
                    " for " + operation + " key " + key);

            int promises = 0;
            int highestAcceptedProposal = -1;
            String highestAcceptedValue = null;

            for (Acceptor acceptor : acceptors) {
                if (acceptor.isFailed()) {
                    continue;
                }

                AcceptorResponse response = acceptor.prepare(proposalNumber);
                if (response.isPromised()) {
                    promises++;

                    if (response.getAcceptedProposalNumber() > highestAcceptedProposal &&
                            response.getAcceptedValue() != null) {
                        highestAcceptedProposal = response.getAcceptedProposalNumber();
                        highestAcceptedValue = response.getAcceptedValue();
                    }
                }
            }

            int majority = getMajority();
            if (promises < majority) {
                System.out.println("Proposer " + serverId + ": Not enough promises (" +
                        promises + "/" + countAvailableAcceptors() + ", need " + majority + "), retrying...");
                retries--;
                try {
                    Thread.sleep(100); // Small delay before retry
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }


            String valueToPropose = value;
            if (highestAcceptedValue != null && !"DELETE".equals(operation)) {
                System.out.println("Proposer " + serverId + ": Using previously accepted value: " +
                        highestAcceptedValue);
                valueToPropose = highestAcceptedValue;
            }

            int accepts = 0;
            for (Acceptor acceptor : acceptors) {
                if (acceptor.isFailed()) {
                    continue; // Skip failed acceptors
                }

                if (acceptor.accept(proposalNumber, valueToPropose)) {
                    accepts++;
                }
            }

            if (accepts < majority) {
                System.out.println("Proposer " + serverId + ": Not enough accepts (" +
                        accepts + "/" + countAvailableAcceptors() + ", need " + majority + "), retrying...");
                retries--;
                try {
                    Thread.sleep(100); // Small delay before retry
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            learner.learn(key, valueToPropose, operation);
            System.out.println("Proposer " + serverId + ": Consensus reached for " +
                    operation + " on key " + key);
            return "SUCCESS: Value accepted by consensus";
        }

        System.out.println("Proposer " + serverId + ": Failed to reach consensus after " +
                MAX_RETRIES + " attempts");
        return "ERROR: Could not reach consensus after multiple attempts";
    }

    private int getMajority() {
        return (countAvailableAcceptors() / 2) + 1;
    }

    private int countAvailableAcceptors() {
        int count = 0;
        for (Acceptor acceptor : acceptors) {
            if (!acceptor.isFailed()) {
                count++;
            }
        }
        return Math.max(count, 1);
    }
}
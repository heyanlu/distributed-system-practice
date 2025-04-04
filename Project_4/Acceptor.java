package Project_4;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Acceptor implements Runnable {
    private final int id;
    private final AtomicInteger highestPromisedProposal = new AtomicInteger(0);
    private final AtomicInteger highestAcceptedProposal = new AtomicInteger(-1);
    private volatile String acceptedValue = null;
    private final AtomicBoolean failed = new AtomicBoolean(false);
    private final Random random = new Random();
    private final ReentrantLock lock = new ReentrantLock();

    public Acceptor() {
        this(0);
    }

    public Acceptor(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        while (true) {
            try {
                int failureDelay = 10000 + random.nextInt(20000);
                Thread.sleep(failureDelay);

                simulateFailure();

                int restartDelay = 500 + random.nextInt(1500);
                Thread.sleep(restartDelay);

                restart();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    public void simulateFailure() {
        failed.set(true);
        System.out.println("Acceptor " + id + ": Failed");
    }


    public void restart() {
        lock.lock();
        try {
            highestPromisedProposal.set(0);
            highestAcceptedProposal.set(-1);
            acceptedValue = null;

            failed.set(false);
            System.out.println("Acceptor " + id + ": Restarted and reset state");
        } finally {
            lock.unlock();
        }
    }


    public AcceptorResponse prepare(int proposalNumber) {
        if (failed.get()) {
            System.out.println("Acceptor " + id + ": Failed, cannot process prepare for proposal " + proposalNumber);
            return new AcceptorResponse(false, -1, null);
        }

        lock.lock();
        try {
            if (proposalNumber > highestPromisedProposal.get()) {
                highestPromisedProposal.set(proposalNumber);
                System.out.println("Acceptor " + id + ": Promised for proposal " + proposalNumber);

                int acceptedProposalNum = highestAcceptedProposal.get();
                return new AcceptorResponse(true, acceptedProposalNum, acceptedProposalNum > 0 ? acceptedValue : null);
            }

            System.out.println("Acceptor " + id + ": Rejected prepare for proposal " + proposalNumber +
                    " (already promised " + highestPromisedProposal.get() + ")");
            return new AcceptorResponse(false, -1, null);
        } finally {
            lock.unlock();
        }
    }

    public boolean accept(int proposalNumber, String value) {
        if (failed.get()) {
            System.out.println("Acceptor " + id + ": Failed, cannot process accept for proposal " + proposalNumber);
            return false;
        }

        lock.lock();
        try {
            if (proposalNumber >= highestPromisedProposal.get()) {
                highestPromisedProposal.set(proposalNumber);
                highestAcceptedProposal.set(proposalNumber);
                acceptedValue = value;
                System.out.println("Acceptor " + id + ": Accepted proposal " + proposalNumber + " with value " + value);
                return true;
            }

            System.out.println("Acceptor " + id + ": Rejected accept for proposal " + proposalNumber +
                    " (promised " + highestPromisedProposal.get() + ")");
            return false;
        } finally {
            lock.unlock();
        }
    }


    public String getAcceptedValue() {
        if (failed.get()) {
            return null;
        }

        lock.lock();
        try {
            return acceptedValue;
        } finally {
            lock.unlock();
        }
    }


    public int getHighestAcceptedProposal() {
        if (failed.get()) {
            return -1;
        }

        lock.lock();
        try {
            return highestAcceptedProposal.get();
        } finally {
            lock.unlock();
        }
    }


    public int getHighestPromisedProposalNumber() {
        if (failed.get()) {
            return 0;
        }

        lock.lock();
        try {
            return highestPromisedProposal.get();
        } finally {
            lock.unlock();
        }
    }


    public int getId() {
        return id;
    }


    public boolean isFailed() {
        return failed.get();
    }
}
package Project_4;


public class AcceptorResponse {
    private final boolean promised;
    private final int acceptedProposalNumber;
    private final String acceptedValue;

    public AcceptorResponse(boolean promised, int acceptedProposalNumber, String acceptedValue) {
        this.promised = promised;
        this.acceptedProposalNumber = acceptedProposalNumber;
        this.acceptedValue = acceptedValue;
    }

    public boolean isPromised() {
        return promised;
    }

    public int getAcceptedProposalNumber() {
        return acceptedProposalNumber;
    }

    public String getAcceptedValue() {
        return acceptedValue;
    }

    @Override
    public String toString() {
        return "AcceptorResponse{promised=" + promised +
                ", acceptedProposalNumber=" + acceptedProposalNumber +
                ", acceptedValue='" + acceptedValue + "'}";
    }
}

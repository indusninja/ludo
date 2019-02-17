package com.fuzzy;

public class CentroidDefuzzifier implements IDefuzzifier{

	// number of intervals to use in numerical approximation
    private int intervals;

    // Parameter: Number of segments that the speech universe will be split to perform the numerical approximation of the center of area.
    public CentroidDefuzzifier( int intervals )
    {
        this.intervals = intervals;
    }
    
    public double Defuzzify(FuzzyOutput fuzzyOutput, INorm normOperator) throws Exception {
		// results and accumulators
        double weightSum = 0, membershipSum = 0;

        // speech universe
        double start = fuzzyOutput.getOutputVariable().getStart();
        double end = fuzzyOutput.getOutputVariable().getEnd();

        // increment
        double increment = ( end - start ) / this.intervals;

        // running through the speech universe and evaluating the labels at each point
        for ( double x = start; x < end; x += increment )
        {
            // we must evaluate x membership to each one of the output labels
            for ( FuzzyOutput.OutputConstraint oc : fuzzyOutput.getOutputList() )
            {
                // getting the membership for X and constraining it with the firing strength
                double membership = fuzzyOutput.getOutputVariable().GetLabelMembership( oc.getLabel(), x );
                double constrainedMembership = normOperator.Evaluate( membership, oc.getFiringStrength() );

                weightSum += x * constrainedMembership;
                membershipSum += constrainedMembership;
            }
        }

        // if no membership was found, then the membershipSum is zero and the numerical output is unknown.
        if ( membershipSum == 0 )
            throw new Exception( "The numerical output in unavaliable. All memberships are zero." );

        return weightSum / membershipSum;
	}

}

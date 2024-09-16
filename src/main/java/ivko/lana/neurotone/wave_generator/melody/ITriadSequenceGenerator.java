package ivko.lana.neurotone.wave_generator.melody;

import ivko.lana.neurotone.processing.Constants;

/**
 * @author Lana Ivko
 */
public interface ITriadSequenceGenerator
{
    static ITriadSequenceGenerator getTriadSequenceGenerator()
    {
        return Constants.DistributorType_.getDistributor();
    }

    Triad[] generateNext(Triad previous);

    Triad[] generateLast(Triad previous);


    enum DistributorType
    {
        SEQUENCED {@Override public ITriadSequenceGenerator getDistributor() {return new TriadSequenceGenerator();}},
        DYADED {@Override public ITriadSequenceGenerator getDistributor() {return new DyadSequenceGenerator();}};

        public abstract ITriadSequenceGenerator getDistributor();
    }
}

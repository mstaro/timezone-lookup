import org.geotools.data.collection.SpatialIndexFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;

import java.io.IOException;
import java.util.Iterator;
import org.geotools.filter.visitor.ExtractBoundsFilterVisitor;
import com.vividsolutions.jts.geom.Envelope;

public class CustomSpatialIndexFeatureCollection extends SpatialIndexFeatureCollection {

    public CustomSpatialIndexFeatureCollection() {
        super();
    }

    public CustomSpatialIndexFeatureCollection(SimpleFeatureType schema) {
        super(schema);
    }

    public CustomSpatialIndexFeatureCollection(SimpleFeatureCollection copy ) throws IOException {
        super(copy);
    }

    @Override
    public SimpleFeatureCollection subCollection(Filter filter) {
// split out the spatial part of the filter
        SpatialIndexFeatureCollection ret = new SpatialIndexFeatureCollection(schema);
        Envelope env = new Envelope();
        env = (Envelope) filter.accept(ExtractBoundsFilterVisitor.BOUNDS_VISITOR, env);
//        if (LOGGER.isLoggable(Level.FINEST)&& Double.isInfinite(env.getWidth())) {
//            LOGGER.fine("Found no spatial element in "+filter);
//            LOGGER.fine("Just going to iterate");
//        }
        for (Iterator<SimpleFeature> iter = (Iterator<SimpleFeature>) index.query(env).iterator(); iter
                .hasNext();) {

            SimpleFeature sample = iter.next();

//            if(LOGGER.isLoggable(Level.FINEST)) {
//                LOGGER.finest("Looking at "+sample);
//            }
            if (filter.evaluate(sample)) {

//                if(LOGGER.isLoggable(Level.FINEST)) {
//                    LOGGER.finest("accepting "+sample);
//                }
                ret.add(sample);
            }
        }

        return ret;
    }


}

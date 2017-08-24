//import com.typesafe.scalalogging.Logger
import com.vividsolutions.jts.geom.{Coordinate, Point}
import com.vividsolutions.jts.index.strtree.STRtree
import org.geotools.data.collection.{ListFeatureCollection, SpatialIndexFeatureCollection, SpatialIndexFeatureSource}
import org.geotools.data.simple.{SimpleFeatureCollection, SimpleFeatureSource}
import org.geotools.geometry.jts.{JTSFactoryFinder, ReferencedEnvelope}
import org.geotools.data.{CachingFeatureSource, DataUtilities, FileDataStoreFinder}
import org.geotools.filter.FilterFactoryImpl

class TimeZoneLookup {}

object TimeZoneLookup {
  private val tzShapeFile = getClass.getResource("/shapefiles/combined_shapefile.shp")

  private val geometryFactory = JTSFactoryFinder.getGeometryFactory()
  private val ff = new FilterFactoryImpl
  private val ds = FileDataStoreFinder.getDataStore(tzShapeFile)
  private val featureSource = ds.getFeatureSource

//  private val tzSource = DataUtilities.source(new ListFeatureCollection(featureSource.getFeatures))

  // caches polygons? doesn't work, methods are not implemented
//  private val tzSource = new SpatialIndexFeatureSource(new SpatialIndexFeatureCollection(featureSource.getFeatures))

  // implemented missing method in CustomSpatialIndexFeatureCollection
  private val tzSource = new SpatialIndexFeatureSource(new CustomSpatialIndexFeatureCollection(featureSource.getFeatures))

//  val logger = Logger[TimeZoneLookup]

  val schemaInfo = {
    val schema = featureSource.getSchema

    s"${schema.getTypeName()}: ${DataUtilities.encodeType(schema)}"
  }

  class TimeZone(tzid: String) {
    val asOption = Option(tzid)
    val asNullable = tzid
  }

  private def lookup(point: Point): SimpleFeatureCollection = {
    val filter = ff.contains(ff.property("the_geom"), ff.literal(point))

//    featureSource.getFeatures(filter)
    tzSource.getFeatures(filter)
  }

  def apply(lat: Double, lng: Double): TimeZone = {
    val point = geometryFactory.createPoint(new Coordinate(lng, lat))

    val it = lookup(point).features()

    try {
      val tzid = if (it.hasNext) {
        val feature = it.next()

        feature.getAttribute("tzid").asInstanceOf[String]
      } else null

      new TimeZone(tzid)
    } catch {
      case e: Throwable => {
//        logger.error(e.getMessage)
        new TimeZone(null)
      }
    } finally {
      it.close()
    }
  }

  def main(args: Array[String]): Unit = {
    val geocodeInputStream = getClass.getResourceAsStream("/US.txt")
    val geocodes = scala.io.Source.fromInputStream(geocodeInputStream).getLines().take(10).map { l =>
      val fields = l.split("""\t""")
      (fields(4).toDouble, fields(5).toDouble)
    } toList

    val tzs = geocodes.flatMap { geo =>
      val start = System.currentTimeMillis
      val tzid = TimeZoneLookup(geo._1, geo._2).asOption
      val end = System.currentTimeMillis

      println(s"lookup for geocode ${geo._1}, ${geo._2} took ${end - start} ms. Timezone -> $tzid")

      tzid
    }
  }
}

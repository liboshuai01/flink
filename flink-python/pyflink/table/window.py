################################################################################
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
# limitations under the License.
################################################################################
from py4j.java_gateway import get_method

from pyflink.java_gateway import get_gateway
from pyflink.table import Expression
from pyflink.table.expression import _get_java_expression
from pyflink.util.api_stability_decorators import PublicEvolving

__all__ = [
    'Tumble',
    'Session',
    'Slide',
    'Over',
    'GroupWindow',
    'OverWindow'
]

from pyflink.table.utils import to_expression_jarray


@PublicEvolving()
class GroupWindow(object):
    """
    A group window specification.

    Group windows group rows based on time or row-count intervals and is therefore essentially a
    special type of groupBy. Just like groupBy, group windows allow to compute aggregates
    on groups of elements.

    Infinite streaming tables can only be grouped into time or row intervals. Hence window
    grouping is required to apply aggregations on streaming tables.

    For finite batch tables, group windows provide shortcuts for time-based groupBy.
    """

    def __init__(self, java_window):
        self._java_window = java_window


@PublicEvolving()
class Tumble(object):
    """
    Helper class for creating a tumbling window. Tumbling windows are consecutive, non-overlapping
    windows of a specified fixed length. For example, a tumbling window of 5 minutes size groups
    elements in 5 minutes intervals.

    Example:
    ::

        >>> from pyflink.table.expressions import col, lit
        >>> Tumble.over(lit(10).minutes) \\
        ...       .on(col("rowtime")) \\
        ...       .alias("w")
    """

    @classmethod
    def over(cls, size: Expression) -> 'TumbleWithSize':
        """
        Creates a tumbling window. Tumbling windows are fixed-size, consecutive, non-overlapping
        windows of a specified fixed length. For example, a tumbling window of 5 minutes size
        groups elements in 5 minutes intervals.

        :param size: The size of the window as time or row-count interval.
        :return: A partially defined tumbling window.
        """
        return TumbleWithSize(get_gateway().jvm.Tumble.over(_get_java_expression(size)))


@PublicEvolving()
class TumbleWithSize(object):
    """
    Tumbling window.

    For streaming tables you can specify grouping by a event-time or processing-time attribute.

    For batch tables you can specify grouping on a timestamp or long attribute.
    """

    def __init__(self, java_window):
        self._java_window = java_window

    def on(self, time_field: Expression) -> 'TumbleWithSizeOnTime':
        """
        Specifies the time attribute on which rows are grouped.

        For streaming tables you can specify grouping by a event-time or processing-ti
        attribute.

        For batch tables you can specify grouping on a timestamp or long attribute.

        :param time_field: Time attribute for streaming and batch tables.
        :return: A tumbling window on event-time/processing-time.
        """
        return TumbleWithSizeOnTime(self._java_window.on(_get_java_expression(time_field)))


@PublicEvolving()
class TumbleWithSizeOnTime(object):
    """
    Tumbling window on time. You need to assign an alias for the window.
    """

    def __init__(self, java_window):
        self._java_window = java_window

    def alias(self, alias: str) -> 'GroupWindow':
        """
        Assigns an alias for this window that the following
        :func:`~pyflink.table.GroupWindowedTable.group_by` and
        :func:`~pyflink.table.WindowGroupedTable.select` clause can refer to.
        :func:`~pyflink.table.WindowGroupedTable.select` statement can access window properties
        such as window start or end time.

        :param alias: Alias for this window.
        :return: This window.
        """
        return GroupWindow(get_method(self._java_window, "as")(alias))


@PublicEvolving()
class Session(object):
    """
    Helper class for creating a session window. The boundary of session windows are defined by
    intervals of inactivity, i.e., a session window is closes if no event appears for a defined
    gap period.

    Example:
    ::

        >>> from pyflink.table.expressions import col, lit
        >>> Session.with_gap(lit(10).minutes) \\
        ...        .on(col("rowtime")) \\
        ...        .alias("w")
    """

    @classmethod
    def with_gap(cls, gap: Expression) -> 'SessionWithGap':
        """
        Creates a session window. The boundary of session windows are defined by
        intervals of inactivity, i.e., a session window is closes if no event appears for a defined
        gap period.

        :param gap: Specifies how long (as interval of milliseconds) to wait for new data before
                    closing the session window.
        :return: A partially defined session window.
        """
        return SessionWithGap(get_gateway().jvm.Session.withGap(_get_java_expression(gap)))


@PublicEvolving()
class SessionWithGap(object):
    """
    Session window.

    For streaming tables you can specify grouping by a event-time or processing-time attribute.

    For batch tables you can specify grouping on a timestamp or long attribute.
    """

    def __init__(self, java_window):
        self._java_window = java_window

    def on(self, time_field: Expression) -> 'SessionWithGapOnTime':
        """
        Specifies the time attribute on which rows are grouped.

        For streaming tables you can specify grouping by a event-time or processing-time
        attribute.

        For batch tables you can specify grouping on a timestamp or long attribute.

        :param time_field: Time attribute for streaming and batch tables.
        :return: A tumbling window on event-time.
        """
        return SessionWithGapOnTime(self._java_window.on(_get_java_expression(time_field)))


@PublicEvolving()
class SessionWithGapOnTime(object):
    """
    Session window on time. You need to assign an alias for the window.
    """

    def __init__(self, java_window):
        self._java_window = java_window

    def alias(self, alias: str) -> 'GroupWindow':
        """
        Assigns an alias for this window that the following
        :func:`~pyflink.table.GroupWindowedTable.group_by` and
        :func:`~pyflink.table.WindowGroupedTable.select` clause can refer to.
        :func:`~pyflink.table.WindowGroupedTable.select` statement can access window properties
        such as window start or end time.

        :param alias: Alias for this window.
        :return: This window.
        """
        return GroupWindow(get_method(self._java_window, "as")(alias))


@PublicEvolving()
class Slide(object):
    """
    Helper class for creating a sliding window. Sliding windows have a fixed size and slide by
    a specified slide interval. If the slide interval is smaller than the window size, sliding
    windows are overlapping. Thus, an element can be assigned to multiple windows.

    For example, a sliding window of size 15 minutes with 5 minutes sliding interval groups
    elements of 15 minutes and evaluates every five minutes. Each element is contained in three
    consecutive window evaluations.

    Example:
    ::

        >>> from pyflink.table.expressions import col, lit
        >>> Slide.over(lit(10).minutes) \\
        ...      .every(lit(5).minutes) \\
        ...      .on(col("rowtime")) \\
        ...      .alias("w")
    """

    @classmethod
    def over(cls, size: Expression) -> 'SlideWithSize':
        """
        Creates a sliding window. Sliding windows have a fixed size and slide by
        a specified slide interval. If the slide interval is smaller than the window size, sliding
        windows are overlapping. Thus, an element can be assigned to multiple windows.

        For example, a sliding window of size 15 minutes with 5 minutes sliding interval groups
        elements of 15 minutes and evaluates every five minutes. Each element is contained in three
        consecutive window evaluations.

        :param size: The size of the window as time or row-count interval.
        :return: A partially specified sliding window.
        """
        return SlideWithSize(get_gateway().jvm.Slide.over(_get_java_expression(size)))


@PublicEvolving()
class SlideWithSize(object):
    """
    Partially specified sliding window. The size of the window either as time or row-count
    interval.
    """

    def __init__(self, java_window):
        self._java_window = java_window

    def every(self, slide: Expression) -> 'SlideWithSizeAndSlide':
        """
        Specifies the window's slide as time or row-count interval.

        The slide determines the interval in which windows are started. Hence, sliding windows can
        overlap if the slide is smaller than the size of the window.

        For example, you could have windows of size 15 minutes that slide by 3 minutes. With this
        15 minutes worth of elements are grouped every 3 minutes and each row contributes to 5
        windows.

        :param slide: The slide of the window either as time or row-count interval.
        :return: A sliding window.
        """
        return SlideWithSizeAndSlide(self._java_window.every(_get_java_expression(slide)))


@PublicEvolving()
class SlideWithSizeAndSlide(object):
    """
    Sliding window. The size of the window either as time or row-count interval.

    For streaming tables you can specify grouping by a event-time or processing-time attribute.

    For batch tables you can specify grouping on a timestamp or long attribute.
    """

    def __init__(self, java_window):
        self._java_window = java_window

    def on(self, time_field: Expression) -> 'SlideWithSizeAndSlideOnTime':
        """
        Specifies the time attribute on which rows are grouped.

        For streaming tables you can specify grouping by a event-time or processing-time
        attribute.

        For batch tables you can specify grouping on a timestamp or long attribute.
        """
        return SlideWithSizeAndSlideOnTime(self._java_window.on(_get_java_expression(time_field)))


@PublicEvolving()
class SlideWithSizeAndSlideOnTime(object):
    """
    Sliding window on time. You need to assign an alias for the window.
    """

    def __init__(self, java_window):
        self._java_window = java_window

    def alias(self, alias: str) -> 'GroupWindow':
        """
        Assigns an alias for this window that the following
        :func:`~pyflink.table.GroupWindowedTable.group_by` and
        :func:`~pyflink.table.WindowGroupedTable.select` clause can refer to.
        :func:`~pyflink.table.WindowGroupedTable.select` statement can access window properties
        such as window start or end time.

        :param alias: Alias for this window.
        :return: This window.
        """
        return GroupWindow(get_method(self._java_window, "as")(alias))


@PublicEvolving()
class Over(object):
    """
    Helper class for creating an over window. Similar to SQL, over window aggregates compute an
    aggregate for each input row over a range of its neighboring rows.

    Over-windows for batch tables are currently not supported.

    Example:
    ::

        >>> from pyflink.table.expressions import col, UNBOUNDED_RANGE
        >>> Over.partition_by(col("a")) \\
        ...     .order_by(col("rowtime")) \\
        ...     .preceding(UNBOUNDED_RANGE) \\
        ...     .alias("w")
    """

    @classmethod
    def order_by(cls, order_by: Expression) -> 'OverWindowPartitionedOrdered':
        """
        Specifies the time attribute on which rows are ordered.

        For streaming tables, reference a rowtime or proctime time attribute here
        to specify the time mode.

        :param order_by: Field reference.
        :return: An over window with defined order.
        """
        return OverWindowPartitionedOrdered(get_gateway().jvm.Over.orderBy(
            _get_java_expression(order_by)))

    @classmethod
    def partition_by(cls, *partition_by: Expression) -> 'OverWindowPartitioned':
        """
        Partitions the elements on some partition keys.

        Each partition is individually sorted and aggregate functions are applied to each
        partition separately.

        :param partition_by: List of field references.
        :return: An over window with defined partitioning.
        """
        return OverWindowPartitioned(get_gateway().jvm.Over.partitionBy(
            to_expression_jarray(partition_by)))


@PublicEvolving()
class OverWindowPartitionedOrdered(object):
    """
    Partially defined over window with (optional) partitioning and order.
    """

    def __init__(self, java_over_window):
        self._java_over_window = java_over_window

    def alias(self, alias: str) -> 'OverWindow':
        """
        Set the preceding offset (based on time or row-count intervals) for over window.

        :param alias: Preceding offset relative to the current row.
        :return: An over window with defined preceding.
        """
        return OverWindow(get_method(self._java_over_window, "as")(alias))

    def preceding(self, preceding: Expression) -> 'OverWindowPartitionedOrderedPreceding':
        """
        Set the preceding offset (based on time or row-count intervals) for over window.

        :param preceding: Preceding offset relative to the current row.
        :return: An over window with defined preceding.
        """
        return OverWindowPartitionedOrderedPreceding(
            self._java_over_window.preceding(_get_java_expression(preceding)))


@PublicEvolving()
class OverWindowPartitionedOrderedPreceding(object):
    """
    Partially defined over window with (optional) partitioning, order, and preceding.
    """

    def __init__(self, java_over_window):
        self._java_over_window = java_over_window

    def alias(self, alias: str) -> 'OverWindow':
        """
        Assigns an alias for this window that the following
        :func:`~pyflink.table.OverWindowedTable.select` clause can refer to.

        :param alias: Alias for this over window.
        :return: The fully defined over window.
        """
        return OverWindow(get_method(self._java_over_window, "as")(alias))

    def following(self, following: Expression) -> 'OverWindowPartitionedOrderedPreceding':
        """
        Set the following offset (based on time or row-count intervals) for over window.

        :param following: Following offset that relative to the current row.
        :return: An over window with defined following.
        """
        return OverWindowPartitionedOrderedPreceding(
            self._java_over_window.following(_get_java_expression(following)))


@PublicEvolving()
class OverWindowPartitioned(object):
    """
    Partially defined over window with partitioning.
    """

    def __init__(self, java_over_window):
        self._java_over_window = java_over_window

    def order_by(self, order_by: Expression) -> 'OverWindowPartitionedOrdered':
        """
        Specifies the time attribute on which rows are ordered.

        For streaming tables, reference a rowtime or proctime time attribute here
        to specify the time mode.

        For batch tables, refer to a timestamp or long attribute.

        :param order_by: Field reference.
        :return: An over window with defined order.
        """
        return OverWindowPartitionedOrdered(self._java_over_window.orderBy(
            _get_java_expression(order_by)))


@PublicEvolving()
class OverWindow(object):
    """
    An over window specification.

    Similar to SQL, over window aggregates compute an aggregate for each input row over a range
    of its neighboring rows.
    """

    def __init__(self, java_over_window):
        self._java_over_window = java_over_window
